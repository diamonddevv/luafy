package dev.diamond.luafy.script.abstraction.api;


import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;

import java.util.HashMap;

public abstract class AbstractScriptApi
{
    public final AbstractScript<?> script;
    public final String name;

    public AbstractScriptApi(AbstractScript<?> script, String name) {
        this.script = script;
        this.name = name;
    }

    public abstract HashMap<String, AdaptableFunction> getFunctions();

}
