package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.registry.callback.ScriptCallbackEvent;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashMap;
import java.util.Map;

public class ScriptApi extends AbstractTypedScriptApi {
    public ScriptApi(AbstractScript<?> script) {
        super(script, "script");
    }


    @Override
    public void getTypedFunctions(TypedFunctionList f) {
        f.add_OptionalParams_Desc("invoke", args -> {
            String id = args[0].asString();
            HashMap<?, ?> ctx = new HashMap<>();
            boolean thread = false;

            if (args.length > 1) ctx = args[1].asMap();
            if (args.length > 2) thread = args[2].asBoolean();


            ServerCommandSource src = script.source;
            AbstractScript<?> s = ScriptManager.getScript(id);
            ScriptManager.execute(id, null, src, ctx, thread, script.name);
            return s.outContextMap == null ? null : s.outContextMap;
        }, "Calls the given script, optionally with context and on its own thread.", Map.class,
                new NamedParam[] {
                        new NamedParam("context", Map.class),
                        new NamedParam("ownThread", Boolean.class)
                },
                new NamedParam("id", String.class)
        );

        f.add_Void_Desc("subscribe_event", args -> {
            String event = args[0].asString();
            String scriptId = args[1].asString();
            boolean threaded = args[2].asBoolean();

            ScriptManager.subscribeEvent(ScriptCallbackEvent.fromStringId(event), scriptId, threaded);
            return null;
        }, "Subscribes the given script to the given event.",
        new NamedParam("event", String.class), new NamedParam("script", String.class), new NamedParam("ownThread", Boolean.class));


        f.add_Void_Desc("unsubscribe_event", args -> {
            String event = args[0].asString();
            String scriptId = args[1].asString();

            ScriptManager.unsubscribeEvent(ScriptCallbackEvent.fromStringId(event), scriptId);
            return null;
        }, "Unsubscribes the given script from the given event.",
                new NamedParam("event", String.class), new NamedParam("script", String.class));
    }

    @Override
    public String getDescription() {
        return "Provides functions relating to Luafy's execution of scripts, including calling scripts, and dynamically subscribing to events.";
    }
}
