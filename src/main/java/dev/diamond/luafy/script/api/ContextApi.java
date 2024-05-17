package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.lua.LuaValueWrapper;
import net.minecraft.server.command.ServerCommandSource;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.HashMap;

public class ContextApi extends AbstractScriptApi {
    public ContextApi(AbstractScript<?, ?> script) {
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

        f.put("scriptcall", args -> {
            String id = args[0].asString();
            HashMap<?, ?> ctx = args[1].asMap();

            ServerCommandSource src = script.source;
            AbstractScript<?, ?> s = ScriptManager.get(id);
            s.execute(src, ctx);
            return s.outContextMap == null ? null : s.outContextMap;
        });

        return f;
    }
}
