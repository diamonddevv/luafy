package dev.diamond.luafy.script.old;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.config.LuafyConfig;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class Old_LuaScript {

    private final String scriptString;
    private final Globals scriptGlobals;
    private final LuaValue script;

    public ServerCommandSource source;
    public LuaTable context;
    public LuaTable outContext;

    public Old_LuaScript(String scriptString) {
        this.scriptString = scriptString;

        this.scriptGlobals = SandboxStrategies.applySandboxStrategy(LuafyConfig.GLOBAL_CONFIG.getStrategy(), this);
        this.script = this.scriptGlobals.load(scriptString);

    }

    public LuaValue execute(ServerCommandSource source, LuaTable ctx) {
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
