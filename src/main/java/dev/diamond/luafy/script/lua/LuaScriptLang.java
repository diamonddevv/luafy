package dev.diamond.luafy.script.lua;


import dev.diamond.luafy.script.registry.lang.ScriptLanguage;

public class LuaScriptLang extends ScriptLanguage<LuaScript> {
    @Override
    public String[] getFileExtensions() {
        return new String[] { "lua" };
    }

    @Override
    public LuaScript readScript(String scriptCode) {
        return new LuaScript(scriptCode);
    }

    @Override
    public String getLanguageDocumentationUrl() {
        return "https://www.lua.org";
    }

    @Override
    public String getImplementerCredits() {
        return "DiamondDev";
    }

    @Override
    public String getDescription() {
        return "The original Luafy Language. Has syntax reminiscent of Python.";
    }
}
