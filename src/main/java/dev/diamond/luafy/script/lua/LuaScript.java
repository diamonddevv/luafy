package dev.diamond.luafy.script.lua;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.api.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.api.ApiProvider;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.SandboxStrategies;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class LuaScript extends AbstractScript<LuaBaseValue> {

    private final String scriptString;
    public final Globals scriptGlobals;
    private final LuaValue script;

    public LuaScript(String scriptString) {
        this.scriptString = scriptString;

        this.scriptGlobals = new Globals();
        SandboxStrategies.applyAbstractSandbox(this);


        this.script = this.scriptGlobals.load(scriptString);
    }

    @Override
    public LuaBaseValue executeScript() {
        var value = execute();
        return new LuaBaseValue(value);
    }

    @Override
    public void addApi(ApiProvider provider) {
        LuaTable table = new LuaTable();
        AbstractScriptApi api = provider.provide(this);

        for (var func : api.getFunctions().entrySet()) {
            table.set(func.getKey(), adaptableToArrArg(func.getValue()));
        }

        scriptGlobals.set(api.name, table);
        if (!scriptGlobals.get("package").isnil())
            scriptGlobals.get("package").get("loaded").set(api.name, table);

    }


    private LuaValue execute() {
        try {
            return this.script.call();
        } catch (LuaError err) {
            Luafy.LOGGER.error("[LUA: INTERPRETATION] \n" + err.getMessage());
        }
        return LuaValue.NIL;
    }

    public static ArrArgFunction adaptableToArrArg(AdaptableFunction adaptableFunction) {
        return new ArrArgFunction()
        {
            @Override
            public LuaValue call(LuaValue[] params) {
                LuaBaseValue[] values = new LuaBaseValue[params.length];
                for (int i = 0; i < params.length; i++) {
                    values[i] = new LuaBaseValue(params[i]);
                }

                var returned = adaptableFunction.call(values);
                LuaBaseValue result = new LuaBaseValue(null);
                result.adaptAndSetOrThrow(returned);
                return result.isNull() ? LuaValue.NIL : result.getValue();
            }
        };
    }

}
