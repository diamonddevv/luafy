package dev.diamond.luafy.script.abstraction.obj;

import dev.diamond.luafy.script.abstraction.TypedFunctions;
import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;

import java.util.HashMap;

public abstract class AbstractTypedScriptObject<T> implements IScriptObject<T>, TypedFunctions {

    private TypedFunctionList list;

    @Override
    public void setTypedFunctionList(TypedFunctionList list) {
        this.list = list;
    }

    @Override
    public TypedFunctionList getTypedFunctionList() {
        return list;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        set = getUntypedFunctions();
    }
}
