package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.BaseValueConversions;
import dev.diamond.luafy.script.abstraction.api.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.util.HexId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class LuafyApi extends AbstractScriptApi {
    public LuafyApi(AbstractScript<?> script) {
        super(script, "luafy");
    }

    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        HashMap<String, AdaptableFunction> f = new HashMap<>();

        f.put("hexid_as_string", args -> HexId.fromString(args[0].asString()));
        f.put("hexid_from_string", args -> HexId.fromString(args[0].asString()));

        f.put("get_system_rtc", args -> System.currentTimeMillis());

        f.put("get_resource", args -> BaseValueConversions.jsonObjToValue(ScriptManager.STATIC_RESOURCES.get(args[0].asString()), args[0]::adapt));
        f.put("get_resource_ids", args -> {
            AdaptableFunction predicateFunction = args[0].asFunction();
            Collection<String> ids = new ArrayList<>();

            ScriptManager.STATIC_RESOURCES.forEach((id, json) -> {
                boolean test = args[0].adapt(predicateFunction.call(BaseValueConversions.jsonObjToValue(json, args[0]::adapt))).asBoolean();
                if (test) ids.add(id);
            });

            return ids;
        });

        return f;
    }
}
