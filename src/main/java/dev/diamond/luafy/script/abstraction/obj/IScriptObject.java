package dev.diamond.luafy.script.abstraction.obj;

import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.util.HexId;

import java.util.HashMap;

public interface IScriptObject<T> {

    HashMap<HexId, IScriptObject<?>> CACHE = new HashMap<>();

    void addFunctions(HashMap<String, AdaptableFunction> set);

    T get();

    static IScriptObject<?> getFromCache(HexId hexid) {
        return hexid.getHashed(CACHE);
    }
}
