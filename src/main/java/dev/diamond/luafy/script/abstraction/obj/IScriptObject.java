package dev.diamond.luafy.script.abstraction.obj;

import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.util.HexId;

import java.util.HashMap;

@FunctionalInterface
public interface IScriptObject {

    HashMap<HexId, IScriptObject> CACHE = new HashMap<>();

    void addFunctions(HashMap<String, AdaptableFunction> set);

    static IScriptObject get(HexId hexid) {
        return hexid.getHashed(CACHE);
    }
}
