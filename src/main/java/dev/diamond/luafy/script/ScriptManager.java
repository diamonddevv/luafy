package dev.diamond.luafy.script;

import com.mojang.brigadier.ParseResults;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.api.obj.EntityScriptObject;
import dev.diamond.luafy.util.HexId;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
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

    public enum CallbackEvent {
        ON_DAY_START("on_daybreak"),
        ON_NIGHTFALL("on_nightfall"),

        TICK("tick"),
        LOAD("load"),

        ON_ENTITY_DIES("on_entity_dies"),
        ON_ENTITY_HURTS("on_entity_hurts"),

        ADVANCEMENT("advancement_obtained"),

        USE_ITEM("on_use")
        ;


        private final String id;
        CallbackEvent(String id) {
            this.id = id;
        }

        public static CallbackEvent get(String string) {
            for (CallbackEvent value : CallbackEvent.values()) {
                if (Objects.equals(value.id, string)) return value;
            } return null;
        }
    }

    public static class Caches {
        public static HashMap<HexId, ParseResults<ServerCommandSource>> PREPARSED_COMMANDS = new HashMap<>();
        public static HashMap<HexId, List<EntityScriptObject>> GROUPED_ENTITIES = new HashMap<>();
    }


    public static final HashMap<String, AbstractScript<?>> SCRIPTS = new HashMap<>();
    public static final Collection<ScriptCallbacks.CallbackScriptBean> CALLBACKS = new ArrayList<>();
    public static HashMap<String, SandboxStrategies.Strategy> SANDBOX_STRATEGIES = new HashMap<>();

    public static final HashMap<CallbackEvent, Collection<String>> EVENT_CALLBACKS = new HashMap<>();

    public static boolean execute(String script, ServerCommandSource src, HashMap<?, ?> ctx) {
        if (!has(script)) return false;
        SCRIPTS.get(script).execute(src, ctx);
        return true;
    }

    public static boolean execute(String script, ServerCommandSource src) {
        return execute(script, src, null);
    }

    public static boolean has(String script) {
        return SCRIPTS.containsKey(script);
    }

    public static AbstractScript<?> get(String script) {
        return SCRIPTS.get(script);
    }

    public static void populateEventCallbacks() {
        EVENT_CALLBACKS.clear();

        for (var v : CallbackEvent.values()) EVENT_CALLBACKS.put(v, new ArrayList<>());

        for (var c : CALLBACKS) {
            for (ScriptCallbacks.CallbackEventBean eventCallback : c.eventCallbacks) {
                CallbackEvent event = CallbackEvent.get(eventCallback.id);
                if (event != null) {
                    EVENT_CALLBACKS.get(event).addAll(eventCallback.scriptIds);
                }
            }
        }
    }

    public static void executeEventCallbacks(CallbackEvent event, ServerCommandSource src, @Nullable Function<Void, HashMap<?, ?>> ctxBuilder) {

        HashMap<?, ?> ctx;
        if (ctxBuilder != null) {
            ctx = ctxBuilder.apply(null);
        } else {
            ctx = null;
        }

        ScriptManager.EVENT_CALLBACKS.get(event)
                .forEach(s -> ScriptManager.execute(s, src, ctx));
    }
}
