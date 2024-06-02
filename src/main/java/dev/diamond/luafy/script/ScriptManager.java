package dev.diamond.luafy.script;

import com.google.gson.JsonObject;
import com.mojang.brigadier.ParseResults;
import dev.diamond.luafy.config.LuafyConfig;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.abstraction.ScriptExecution;
import dev.diamond.luafy.script.api.obj.entity.EntityScriptObject;
import dev.diamond.luafy.script.registry.callback.CallbackEventSubscription;
import dev.diamond.luafy.script.registry.callback.ScriptCallbackEvent;
import dev.diamond.luafy.script.registry.callback.ScriptCallbacks;
import dev.diamond.luafy.script.registry.sandbox.Strategy;
import dev.diamond.luafy.util.HexId;
import dev.diamond.luafy.util.RemovalMarkedRunnable;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ScriptManager {


    public enum ExplicitType {
        DOUBLE("NUM"), STRING("STR"), BOOLEAN("BOOL"), OBJECT("OBJ"), LIST("LIST");

        public String str;
        ExplicitType(String str) {
            this.str = str;
        }

        public static ExplicitType get(String string) {
            for (ExplicitType value : ExplicitType.values()) {
                if (Objects.equals(value.str, string)) return value;
            } return null;
        }
    }


    // Caches for data used BY scripts
    public static class ScriptCaches {
        public static HashMap<HexId, ParseResults<ServerCommandSource>> PREPARSED_COMMANDS = new HashMap<>();
        public static HashMap<HexId, List<EntityScriptObject>> GROUPED_ENTITIES = new HashMap<>();
    }

    // Script-Related Resource Caches
    public static final HashMap<String, AbstractScript<?>> SCRIPTS = new HashMap<>();
    public static final Collection<ScriptCallbacks.CallbackScriptBean> CALLBACK_FILES = new ArrayList<>();
    public static final HashMap<String, Strategy> SANDBOX_STRATEGIES = new HashMap<>();
    public static final HashMap<String, JsonObject> STATIC_RESOURCES = new HashMap<>();

    // Callbacks
    public static final HashMap<ScriptCallbackEvent, Collection<CallbackEventSubscription>> EVENT_CALLBACKS = new HashMap<>();

    // Script Threading
    public static final ConcurrentLinkedQueue<ScriptExecution> SCRIPT_THREAD_EXECUTIONS = new ConcurrentLinkedQueue<>();
    public static final ConcurrentLinkedQueue<RemovalMarkedRunnable> SERVER_THREAD_EXECUTIONS = new ConcurrentLinkedQueue<>();

    private static final Thread scriptThread = new Thread(null, () -> {
       while (true) {
           if (!SCRIPT_THREAD_EXECUTIONS.isEmpty()) {
               SCRIPT_THREAD_EXECUTIONS.forEach(ScriptExecution::execute);
           }
           SCRIPT_THREAD_EXECUTIONS.removeIf(ScriptExecution::isMarkedForRemoval);
       }
    }, "Master Script Thread");


    // Script Executors
    public static boolean executeCurrentThread(String script, ServerCommandSource src, HashMap<?, ?> ctx, String caller) {
        if (!hasScript(script)) return false;

        if (ctx == null) ctx = new HashMap<>();
        ((HashMap<String, Object>)ctx).put("caller", caller);

        SCRIPTS.get(script).execute(src, ctx);
        return true;
    }
    public static boolean execute(String script, ServerCommandSource src, HashMap<?, ?> ctx, boolean ownThread, String caller) {

        if (LuafyConfig.GLOBAL_CONFIG.scriptThreading) {
            if (ownThread) {

                Thread t = new Thread(null, () -> {
                    executeCurrentThread(script, src, ctx, caller);
                }, "Script Thread");

                t.setDaemon(true);
                t.start();

                return true;
            }
        }
        SCRIPT_THREAD_EXECUTIONS.add(ScriptExecution.of(script, src, ctx, caller));
        return true;
    }
    public static boolean execute(String script, ServerCommandSource src, boolean ownThread, String caller) {
        return execute(script, src, null, ownThread, caller);
    }

    // Script Cache
    public static boolean hasScript(String script) {
        return SCRIPTS.containsKey(script);
    }
    public static AbstractScript<?> getScript(String script) {
        return SCRIPTS.get(script);
    }

    // Callbacks
    public static void populateEventCallbacks() {
        EVENT_CALLBACKS.clear();

        for (var v : ScriptCallbackEvent.getAll()) EVENT_CALLBACKS.put(v, new ArrayList<>());

        for (var c : CALLBACK_FILES) {
            for (ScriptCallbacks.CallbackEventBean eventCallback : c.eventCallbacks) {
                ScriptCallbackEvent event = ScriptCallbackEvent.fromStringId(eventCallback.id);
                if (event != null) {
                    EVENT_CALLBACKS.get(event).addAll(eventCallback.scriptIds.stream().map(s ->
                            CallbackEventSubscription.of(s, eventCallback.ownThread)).toList());
                }
            }
        }
    }
    public static void executeEventCallbacks(ScriptCallbackEvent event, Supplier<ServerCommandSource> src, @Nullable Consumer<HashMap<String, Object>> ctxBuilder) {

        HashMap<String, Object> ctx;
        if (ctxBuilder != null) {
            ctx = new HashMap<>();
            ctxBuilder.accept(ctx);
        } else {
            ctx = null;
        }

        if (!ScriptManager.EVENT_CALLBACKS.containsKey(event)) return;

        ScriptManager.EVENT_CALLBACKS.get(event)
                .forEach(s -> ScriptManager.execute(s.getScriptId(), src.get(), ctx, s.usesOwnThread(), "$server"));
    }

    public static void subscribeEvent(ScriptCallbackEvent event, String scriptId, boolean usesOwnThread) {
        EVENT_CALLBACKS.get(event).add(CallbackEventSubscription.of(scriptId, usesOwnThread));
    }
    public static void unsubscribeEvent(ScriptCallbackEvent event, String scriptId) {
        EVENT_CALLBACKS.get(event).removeIf(e -> Objects.equals(e.getScriptId(), scriptId));
    }


    // Threading
    public static void startScriptThread() {
        scriptThread.start();
    }

}
