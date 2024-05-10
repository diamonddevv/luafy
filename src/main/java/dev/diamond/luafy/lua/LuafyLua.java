package dev.diamond.luafy.lua;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LuafyLua {

    public static class ArgTypes {
        public static final String
                NUMBER = "NUM", STRING = "STR", TABLE = "OBJ", BOOL = "BOOL", LIST = "LIST";
    }

    public static HashMap<String, LuaScript> LUA_SCRIPTS = new HashMap<>();

    public static List<CallbackScriptBean> CALLBACK_SCRIPTS = new ArrayList<>();

    public static class CallbackScriptBean {
        @SerializedName("advancements")
        public List<IdentifierCallbackBean> advancementCallbacks = null;


    }

    public static class ScriptsBean {
        @SerializedName("scripts")
        public List<String> scriptIds;
    }

    public static class IdentifierCallbackBean {
        @SerializedName("id")
        public String id;

        @SerializedName("scripts")
        public List<String> scriptIds;
    }

}
