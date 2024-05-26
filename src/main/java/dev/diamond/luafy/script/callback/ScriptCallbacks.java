package dev.diamond.luafy.script.callback;

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
    public static final ScriptCallbackEvent ON_STAT_CHANGED =           new ScriptCallbackEvent(Luafy.id("stat_changes"));
    public static final ScriptCallbackEvent ON_GAME_EVENT =             new ScriptCallbackEvent(Luafy.id("game_event_emits"));

    public static void registerAll() {
        TICK.register();
        LOAD.register();

        ON_DAY_START.register();
        ON_NIGHT_START.register();
        ON_ENTITY_DIES.register();
        ON_ENTITY_HURTS.register();
        ON_ADVANCEMENT_OBTAINED.register();
        ON_ITEM_USED.register();
        ON_STAT_CHANGED.register();
        ON_GAME_EVENT.register();
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
