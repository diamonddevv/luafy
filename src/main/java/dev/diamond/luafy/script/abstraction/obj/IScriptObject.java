package dev.diamond.luafy.script.abstraction.obj;

import dev.diamond.luafy.script.abstraction.AdaptableFunction;

import java.util.HashMap;

@FunctionalInterface
public interface IScriptObject {
    void addFunctions(HashMap<String, AdaptableFunction> set);
}
