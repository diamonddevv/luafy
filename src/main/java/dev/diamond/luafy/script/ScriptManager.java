package dev.diamond.luafy.script;

import com.mojang.brigadier.ParseResults;
import dev.diamond.luafy.config.LuafyConfig;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.abstraction.ScriptExecution;
import dev.diamond.luafy.script.api.obj.EntityScriptObject;
import dev.diamond.luafy.script.callback.ScriptCallbackEvent;
import dev.diamond.luafy.script.callback.ScriptCallbacks;
import dev.diamond.luafy.util.HexId;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

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


    public static class Caches {
        public static HashMap<HexId, ParseResults<ServerCommandSource>> PREPARSED_COMMANDS = new HashMap<>();
        public static HashMap<HexId, List<EntityScriptObject>> GROUPED_ENTITIES = new HashMap<>();
    }

    public static final HashMap<String, AbstractScript<?>> SCRIPTS = new HashMap<>();
    public static final Collection<ScriptCallbacks.CallbackScriptBean> CALLBACKS = new ArrayList<>();
    public static final HashMap<String, SandboxStrategies.Strategy> SANDBOX_STRATEGIES = new HashMap<>();
    public static final HashMap<ScriptCallbackEvent, Collection<Pair<String, Boolean>>> EVENT_CALLBACKS = new HashMap<>();

    public static final ConcurrentLinkedQueue<ScriptExecution> SCRIPT_THREAD_EXECUTIONS = new ConcurrentLinkedQueue<>();
    private static final Thread scriptThread = new Thread(null, () -> {
       while (true) {
           if (!SCRIPT_THREAD_EXECUTIONS.isEmpty()) {
               SCRIPT_THREAD_EXECUTIONS.forEach(ScriptExecution::execute);
           }
           SCRIPT_THREAD_EXECUTIONS.removeIf(ScriptExecution::isMarkedForRemoval);
       }
    }, "Master Script Thread");


    public static boolean executeCurrentThread(String script, ServerCommandSource src, HashMap<?, ?> ctx) {
        if (!has(script)) return false;
        SCRIPTS.get(script).execute(src, ctx);
        return true;
    }
    public static boolean execute(String script, ServerCommandSource src, HashMap<?, ?> ctx, boolean ownThread) {
        if (LuafyConfig.GLOBAL_CONFIG.scriptThreading) {
            if (ownThread) {

                Thread t = new Thread(null, () -> {
                    executeCurrentThread(script, src, ctx);
                }, "Script Thread");

                t.setDaemon(true);
                t.start();

                return true;
            }
        }
        SCRIPT_THREAD_EXECUTIONS.add(ScriptExecution.of(script, src, ctx));
        return true;
    }
    public static boolean execute(String script, ServerCommandSource src, boolean ownThread) {
        return execute(script, src, null, ownThread);
    }

    public static boolean has(String script) {
        return SCRIPTS.containsKey(script);
    }
    public static AbstractScript<?> get(String script) {
        return SCRIPTS.get(script);
    }

    public static void populateEventCallbacks() {
        EVENT_CALLBACKS.clear();

        for (var v : ScriptCallbackEvent.getAll()) EVENT_CALLBACKS.put(v, new ArrayList<>());

        for (var c : CALLBACKS) {
            for (ScriptCallbacks.CallbackEventBean eventCallback : c.eventCallbacks) {
                ScriptCallbackEvent event = ScriptCallbackEvent.fromStringId(eventCallback.id);
                if (event != null) {
                    EVENT_CALLBACKS.get(event).addAll(eventCallback.scriptIds.stream().map(s -> new Pair<>(s, eventCallback.ownThread)).toList());
                }
            }
        }
    }
    public static void executeEventCallbacks(ScriptCallbackEvent event, ServerCommandSource src, @Nullable Function<Void, HashMap<?, ?>> ctxBuilder) {

        HashMap<?, ?> ctx;
        if (ctxBuilder != null) {
            ctx = ctxBuilder.apply(null);
        } else {
            ctx = null;
        }

        if (!ScriptManager.EVENT_CALLBACKS.containsKey(event)) return;

        ScriptManager.EVENT_CALLBACKS.get(event)
                .forEach(s -> ScriptManager.execute(s.getLeft(), src, ctx, s.getRight()));
    }


    public static void startScriptThread() {
        scriptThread.start();
    }

}
