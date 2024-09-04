package dev.diamond.luafy.script.lua;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.api.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.api.ApiProvider;
import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.api.LuafyApi;
import dev.diamond.luafy.script.registry.lang.ScriptLanguage;
import dev.diamond.luafy.script.registry.lang.ScriptLanguages;
import dev.diamond.luafy.script.registry.sandbox.Apis;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Collection;
import java.util.List;

public class LuaScript extends AbstractScript<LuaBaseValue> {

    private final String scriptString;
    public final Globals scriptGlobals;
    private final LuaValue script;

    public LuaScript(String scriptString) {
        this.scriptString = scriptString;

        this.scriptGlobals = new Globals();
        Apis.applyAbstractSandbox(this);


        this.script = this.scriptGlobals.load(scriptString);
    }

    @Override
    public LuaBaseValue executeScript() {
        var value = execute();
        return new LuaBaseValue(value);
    }

    @Override
    public LuaBaseValue executeScriptFunction(String functionName, Collection<LuaBaseValue> params) {
        LuaValue[] p = new LuaValue[params.size()];
        List<LuaBaseValue> a = params.stream().toList();
        for (int i = 0; i < params.size(); i++) {
            p[i] = a.get(i).value;
        }

        var value = executeFunction(functionName, p);
        return new LuaBaseValue(value);
    }

    @Override
    public void addApi(ApiProvider provider) {
        LuaTable table = new LuaTable();
        AbstractScriptApi api = provider.provide(this);

        for (var func : api.getFunctions().entrySet()) {
            table.set(func.getKey(), adaptableToArrArg(func.getValue()));
        }

        if (api instanceof LuafyApi luafy) {
            this.scriptGlobals.set("require", adaptableToArrArg(luafy.getFunctions().get("include"))); // override default require
        }

        scriptGlobals.set(api.name, table);
        if (!scriptGlobals.get("package").isnil())
            scriptGlobals.get("package").get("loaded").set(api.name, table);

    }

    @Override
    public LuaBaseValue getNullBaseValue() {
        return new LuaBaseValue(null);
    }

    @Override
    public ScriptLanguage<?> getLanguage() {
        return ScriptLanguages.LUA;
    }


    private LuaValue execute() {
        try {
            return this.script.call();
        } catch (LuaError err) {
            Luafy.LOGGER.error("[LUA: INTERPRETATION] \n" + err.getMessage());
        }
        return LuaValue.NIL;
    }

    private LuaValue executeFunction(String function, LuaValue[] params) {
        try {
            LuaTable table = this.script.call().checktable();
            return table.get(function).invoke(LuaValue.varargsOf(params)).arg1();
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
