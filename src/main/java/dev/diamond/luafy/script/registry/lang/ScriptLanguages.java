package dev.diamond.luafy.script.registry.lang;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.lua.LuaScriptLang;
import net.minecraft.registry.Registry;

public class ScriptLanguages {

    public static final LuaScriptLang LUA = new LuaScriptLang();

    public static void registerAll() {
        Registry.register(Luafy.Registries.SCRIPT_LANG_REGISTRY, Luafy.id("lua"), LUA);
    }
}
