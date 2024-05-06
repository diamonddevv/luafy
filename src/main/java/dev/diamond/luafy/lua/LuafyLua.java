package dev.diamond.luafy.lua;


import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.HashMap;

public class LuafyLua {

    public static class ArgTypes {
        public static final String
                NUMBER = "NUM", STRING = "STR", TABLE = "OBJ", BOOL = "BOOL";
    }

    public static HashMap<String, LuaScriptManager> LUA_SCRIPTS = new HashMap<>();


}
