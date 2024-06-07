package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.api.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;

import java.util.HashMap;

public class ContextApi extends AbstractScriptApi {
    public ContextApi(AbstractScript<?> script) {
        super(script, "context");
    }


    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        HashMap<String, AdaptableFunction> f = new HashMap<>();

        f.put("get", args -> {
            if (script.contextMap != null) {
                return script.contextMap;
            } else return null;
        });

        f.put("set_outctx", args ->  {
            script.outContextMap = args[0].asMap();
            return null;
        });

        return f;
    }
}
