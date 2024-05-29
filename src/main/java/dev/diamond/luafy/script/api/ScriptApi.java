package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.api.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.registry.callback.ScriptCallbackEvent;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashMap;

public class ScriptApi extends AbstractScriptApi {
    public ScriptApi(AbstractScript<?> script) {
        super(script, "script");
    }

    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {

        HashMap<String, AdaptableFunction> f = new HashMap<>();

        f.put("invoke", args -> {
            String id = args[0].asString();
            HashMap<?, ?> ctx = new HashMap<>();
            boolean thread = false;

            if (args.length > 1) ctx = args[1].asMap();
            if (args.length > 2) thread = args[2].asBoolean();


            ServerCommandSource src = script.source;
            AbstractScript<?> s = ScriptManager.getScript(id);
            ScriptManager.execute(id, src, ctx, thread, script.name);
            return s.outContextMap == null ? null : s.outContextMap;
        });

        f.put("subscribe_event", args -> {
            String event = args[0].asString();
            String scriptId = args[1].asString();
            boolean threaded = args[2].asBoolean();

            ScriptManager.subscribeEvent(ScriptCallbackEvent.fromStringId(event), scriptId, threaded);
            return null;
        });


        f.put("unsubscribe_event", args -> {
            String event = args[0].asString();
            String scriptId = args[1].asString();

            ScriptManager.unsubscribeEvent(ScriptCallbackEvent.fromStringId(event), scriptId);
            return null;
        });




        return f;
    }
}
