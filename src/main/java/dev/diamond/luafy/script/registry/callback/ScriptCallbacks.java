package dev.diamond.luafy.script.registry.callback;

import com.google.gson.annotations.SerializedName;
import dev.diamond.luafy.Luafy;

import java.util.List;

public class ScriptCallbacks {


    public static final ScriptCallbackEvent TICK =                      new ScriptCallbackEvent(Luafy.id("tick"));
    public static final ScriptCallbackEvent LOAD =                      new ScriptCallbackEvent(Luafy.id("load"));
    public static final ScriptCallbackEvent ON_DAY_START =              new ScriptCallbackEvent(Luafy.id("daybreak"));
    public static final ScriptCallbackEvent ON_NIGHT_START =            new ScriptCallbackEvent(Luafy.id("nightfall"));
    public static final ScriptCallbackEvent ON_ENTITY_DIES =            new ScriptCallbackEvent(Luafy.id("entity_dies"));
    public static final ScriptCallbackEvent ON_ENTITY_HURTS =           new ScriptCallbackEvent(Luafy.id("entity_hurts"));
    public static final ScriptCallbackEvent ON_ADVANCEMENT_OBTAINED =   new ScriptCallbackEvent(Luafy.id("advancement_obtained"));
    public static final ScriptCallbackEvent ON_ITEM_USED =              new ScriptCallbackEvent(Luafy.id("item_used"));
    public static final ScriptCallbackEvent ON_ITEM_USED_ON_BLOCK =     new ScriptCallbackEvent(Luafy.id("item_used_on_block"));
    public static final ScriptCallbackEvent ON_ITEM_USED_ON_ENTITY =    new ScriptCallbackEvent(Luafy.id("item_used_on_entity"));
    public static final ScriptCallbackEvent ON_STAT_CHANGED =           new ScriptCallbackEvent(Luafy.id("stat_changes"));
    public static final ScriptCallbackEvent ON_GAME_EVENT =             new ScriptCallbackEvent(Luafy.id("game_event_emits"));
    public static final ScriptCallbackEvent HAND_SWINGS =               new ScriptCallbackEvent(Luafy.id("hand_swings"));
    public static final ScriptCallbackEvent TRY_ATTACK =                new ScriptCallbackEvent(Luafy.id("attack_tried"));
    public static final ScriptCallbackEvent EFFECT_APPLIED =            new ScriptCallbackEvent(Luafy.id("effect_applied"));
    public static final ScriptCallbackEvent EFFECT_LOST =               new ScriptCallbackEvent(Luafy.id("effect_lost"));
    public static final ScriptCallbackEvent REGENERATES_HEALTH =        new ScriptCallbackEvent(Luafy.id("health_regenerated"));
    public static final ScriptCallbackEvent EATS =                      new ScriptCallbackEvent(Luafy.id("eats"));
    public static final ScriptCallbackEvent EXHAUST =                   new ScriptCallbackEvent(Luafy.id("exhuasts"));
    public static final ScriptCallbackEvent SPAWN =                     new ScriptCallbackEvent(Luafy.id("spawns"));
    public static final ScriptCallbackEvent TICK_ENTITY =               new ScriptCallbackEvent(Luafy.id("entity_ticks"));
    public static final ScriptCallbackEvent CONNECTS_TO_SERVER =        new ScriptCallbackEvent(Luafy.id("connects_to_server"));
    public static final ScriptCallbackEvent DISCONNECTS_FROM_SERVER =   new ScriptCallbackEvent(Luafy.id("disconnects_from_server"));
    public static final ScriptCallbackEvent SERVER_CLOSES =             new ScriptCallbackEvent(Luafy.id("server_closes"));

    public static void registerAll() {
        TICK.register();
        LOAD.register();

        ON_DAY_START.register();
        ON_NIGHT_START.register();
        ON_ENTITY_DIES.register();
        ON_ENTITY_HURTS.register();
        ON_ADVANCEMENT_OBTAINED.register();
        ON_ITEM_USED.register();
        ON_ITEM_USED_ON_BLOCK.register();
        ON_ITEM_USED_ON_ENTITY.register();
        ON_STAT_CHANGED.register();
        ON_GAME_EVENT.register();
        HAND_SWINGS.register();
        TRY_ATTACK.register();

        EFFECT_APPLIED.register();
        EFFECT_LOST.register();
        REGENERATES_HEALTH.register();
        EATS.register();
        EXHAUST.register();
        SPAWN.register();
        TICK_ENTITY.register();
        CONNECTS_TO_SERVER.register();
        DISCONNECTS_FROM_SERVER.register();
        SERVER_CLOSES.register();
    }


    public static class CallbackScriptBean {

        @SerializedName("events")
        public List<CallbackEventBean> eventCallbacks;

    }

    public static class CallbackEventBean {
        @SerializedName("event")
        public String id;

        @SerializedName("scripts")
        public List<String> scriptIds;

        @SerializedName("use_own_thread")
        public boolean ownThread = false;
    }


}
