package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.abstraction.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.util.HexId;

import java.util.HashMap;

public class StorageApi extends AbstractScriptApi {
    public StorageApi(AbstractScript<?, ?, ?> script) {
        super(script, "luafy");
    }

    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        HashMap<String, AdaptableFunction> f = new HashMap<>();

        f.put("hexid_as_string", args -> HexId.fromString(args[0].asString()));
        f.put("hexid_from_string", args -> HexId.fromString(args[0].asString()));

        return f;
    }
}
