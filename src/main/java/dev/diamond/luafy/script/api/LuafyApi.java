package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.api.obj.util.ByteBufScriptObject;

import java.util.ArrayList;
import java.util.Collection;

public class LuafyApi extends AbstractTypedScriptApi {
    public LuafyApi(AbstractScript<?> script) {
        super(script, "luafy");
    }

    /*@Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        HashMap<String, AdaptableFunction> f = new HashMap<>();

        f.put("get_system_rtc", args -> System.currentTimeMillis());

        f.put("get_resource", args -> new ByteBufScriptObject(ScriptManager.STATIC_RESOURCES.get(args[0].asString())));
        f.put("get_resource_ids", LuafyApi::getResourceIds);

        return f;
    }*/

    @Override
    public void getTypedFunctions(TypedFunctionList f) {
        f.addNoParams           ("get_system_rtc", args -> System.currentTimeMillis(), Long.class);
        f.add                   ("get_resource", args -> new ByteBufScriptObject(ScriptManager.STATIC_RESOURCES.get(args[0].asString())), ByteBufScriptObject.class, String.class);
        f.addWithOptionalParams ("get_resource_ids", LuafyApi::getResourceIds, Collection.class, new Class[] {Boolean.class}, AdaptableFunction.class);
    }

    public static Object getResourceIds(AbstractBaseValue<?, ?>... args) {
        AdaptableFunction predicateFunction = args[0].asFunction();
        boolean includeData = args.length > 1 && args[1].asBoolean();
        Collection<String> ids = new ArrayList<>();

        ScriptManager.STATIC_RESOURCES.forEach((id, buf) -> {
            boolean test = args[0].adapt(
                    predicateFunction.call(
                            args[0].adapt(id),
                            includeData ? args[0].adapt(new ByteBufScriptObject(buf)) : null
                    )
            ).asBoolean();
            if (test) ids.add(id);
        });

        return ids;
    }
}
