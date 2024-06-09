package dev.diamond.luafy.script.abstraction.api;

import dev.diamond.luafy.util.DescriptionProvider;
import dev.diamond.luafy.script.abstraction.TypedFunctions;
import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;

import java.util.HashMap;

public abstract class AbstractTypedScriptApi extends AbstractScriptApi implements TypedFunctions, DescriptionProvider {

    public TypedFunctionList typedFunctionList;

    public AbstractTypedScriptApi(AbstractScript<?> script, String name) {
        super(script, name);
    }

    @Override
    public void setTypedFunctionList(TypedFunctionList list) {
        typedFunctionList = list;
    }

    @Override
    public TypedFunctionList getTypedFunctionList() {
        return typedFunctionList;
    }

    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        return getUntypedFunctions();
    }

}
