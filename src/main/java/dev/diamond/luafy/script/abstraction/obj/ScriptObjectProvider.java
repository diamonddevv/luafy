package dev.diamond.luafy.script.abstraction.obj;

@FunctionalInterface
public interface ScriptObjectProvider {
    IScriptObject provide();
}
