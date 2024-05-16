package dev.diamond.luafy.script.lua;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.ApiProvider;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.old.ArrArgFunction;
import dev.diamond.luafy.script.SandboxStrategies;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaScript extends AbstractScript<LuaFunctionWrapper, LuaTableWrapper, LuaValueWrapper> {

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
    public LuaValueWrapper executeScript() {
        var value = execute();
        return new LuaValueWrapper(value);
    }

    @Override
    public void addApi(ApiProvider provider) {
        LuaTable table = new LuaTable();
        AbstractScriptApi api = provider.provide(this);

        for (var func : api.getFunctions().entrySet()) {
            table.set(func.getKey(), new ArrArgFunction()
                    {
                        @Override
                        public LuaValue call(LuaValue[] params) {
                            LuaValueWrapper[] values = new LuaValueWrapper[params.length];
                            for (int i = 0; i < params.length; i++) {
                                values[i] = new LuaValueWrapper(params[i]);
                            }

                            var returned = func.getValue().call(values);
                            LuaValueWrapper result = new LuaValueWrapper(null);
                            result.adaptAndSetOrThrow(returned);
                            return result.isNull() ? LuaValue.NIL : result.getValue();
                        }
                    }
            );
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

}
