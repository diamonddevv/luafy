package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.abstraction.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.lua.LuaHexid;
import dev.diamond.luafy.script.old.api.OldCommandApi;
import dev.diamond.luafy.util.HexId;

import java.util.HashMap;

public class CommandApi extends AbstractScriptApi {


    public CommandApi(AbstractScript<?, ?, ?> script) {
        super(script, "command");
    }

    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        HashMap<String, AdaptableFunction> f = new HashMap<>();

        f.put("execute", args -> {
            var parsed = OldCommandApi.parseCommand(args[0].asString(), script.source);
            return OldCommandApi.executeCommand(parsed, script.source);
        });

        f.put("parse", args -> {
            var parsed = OldCommandApi.parseCommand(args[0].asString(), script.source);
            var hexid = HexId.makeNewUnique(ScriptManager.Caches.PREPARSED_COMMANDS.keySet());
            ScriptManager.Caches.PREPARSED_COMMANDS.put(hexid, parsed);

            return hexid;
        });

        f.put("execute_preparsed", args -> {
            var hi = HexId.fromString(args[0].asString());
            var parse = hi.getHashed(ScriptManager.Caches.PREPARSED_COMMANDS);
            return OldCommandApi.executeCommand(parse, script.source);
        });

        f.put("free_preparsed", args -> {
            var hi = HexId.fromString(args[0].asString());
            hi.removeHashed(ScriptManager.Caches.PREPARSED_COMMANDS);
            return null;
        });

        return f;
    }

}
