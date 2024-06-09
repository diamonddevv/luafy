package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;

import java.util.Map;

public class ContextApi extends AbstractTypedScriptApi {
    public ContextApi(AbstractScript<?> script) {
        super(script, "context");
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {
        f.add_NoParams(
                "get", args -> {
                    if (script.contextMap != null) {
                        return script.contextMap;
                    } else return null;
                }, Map.class);

        f.add_Void(
                "set_outctx", args -> {
                    script.outContextMap = args[0].asMap();
                    return null;
                }, new NamedParam("context", Map.class));
    }

    @Override
    public String getDescription() {
        return "Controls the context passed in and out of scripts.";
    }
}
