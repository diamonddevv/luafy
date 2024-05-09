package dev.diamond.luafy.lua;


import java.util.HashMap;

public class LuafyLua {

    public static class ArgTypes {
        public static final String
                NUMBER = "NUM", STRING = "STR", TABLE = "OBJ", BOOL = "BOOL", LIST = "LIST";
    }

    public static HashMap<String, LuaScript> LUA_SCRIPTS = new HashMap<>();


}
