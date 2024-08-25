package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.api.obj.util.ByteBufScriptObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LuafyApi extends AbstractTypedScriptApi {
    public LuafyApi(AbstractScript<?> script) {
        super(script, "luafy");
    }


    @Override
    public void getTypedFunctions(TypedFunctionList f) {
        f.add_NoParams_Desc(
                "get_system_rtc", args -> System.currentTimeMillis(),
                "Gets the Server System RTC (Real Time Clock). This is expressed in Milliseconds since the Epoch. (00:00 UTC, Jan 1 1970)",
                Long.class
        );

        f.add(
                "get_resource", args -> new ByteBufScriptObject(ScriptManager.STATIC_RESOURCES.get(args[0].asString())),
                ByteBufScriptObject.class,
                new NamedParam("id", String.class)
        );

        f.add_OptionalParams(
                "get_resource_ids", LuafyApi::getResourceIds,
                List.class,
                new NamedParam[] {new NamedParam("includeDataParameter", Boolean.class)},
                new NamedParam.FunctionParam("predicate",
                        new NamedParam[0],
                        new NamedParam[] {new NamedParam("data", ByteBufScriptObject.class)},
                        Boolean.class
                )
        );
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

    @Override
    public String getDescription() {
        return "Provides miscellaneous functions such as access to resources and server hardware-based functions.";
    }
}
