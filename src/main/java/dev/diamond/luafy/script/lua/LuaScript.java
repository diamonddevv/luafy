package dev.diamond.luafy.script.lua;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.AbstractScript;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class LuaScript extends AbstractScript<LuaFunctionWrapper, LuaTableWrapper, LuaValueWrapper> {

    private final String scriptString;
    private final Globals scriptGlobals;
    private final LuaValue script;

    public ServerCommandSource source;
    public LuaTable context;
    public LuaTable outContext;


    public LuaScript(String scriptString) {
        this.scriptString = scriptString;

        //this.scriptGlobals = SandboxStrategies.applySandboxStrategy(LuafyConfig.GLOBAL_CONFIG.getStrategy(), this);
        //this.script = this.scriptGlobals.load(scriptString);

        this.scriptGlobals = null;
        this.script = null;
    }

    @Override
    public LuaValueWrapper execute(ServerCommandSource source, LuaTableWrapper contextMap) {
        var value = execute(source, contextMap.value);
        return new LuaValueWrapper(value);
    }

    private LuaValue execute(ServerCommandSource source, LuaTable ctx) {
        try {
            this.source = FunctionCommand.createFunctionCommandSource(source);
            this.context = ctx;

            return this.script.call();
        } catch (LuaError err) {
            Luafy.LOGGER.error("[LUA: INTERPRETATION] \n" + err.getMessage());
        }
        return LuaValue.NIL;
    }

}
