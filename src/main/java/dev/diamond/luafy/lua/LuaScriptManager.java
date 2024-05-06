package dev.diamond.luafy.lua;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.lua.lib.CommandApi;
import dev.diamond.luafy.lua.lib.ServerApi;
import dev.diamond.luafy.lua.lib.StorageApi;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaScriptManager {

    private final String scriptString;
    private final Globals scriptGlobals;
    private final LuaValue script;

    public ServerCommandSource source;

    public LuaScriptManager(String scriptString) {
        this.scriptString = scriptString;

        this.scriptGlobals = JsePlatform.standardGlobals();
        loadFunctions();
        LuaC.install(this.scriptGlobals);
        this.script = this.scriptGlobals.load(scriptString);

    }

    private void loadFunctions() {
        // libs
        scriptGlobals.load(new CommandApi(this));
        scriptGlobals.load(new StorageApi(this));
        scriptGlobals.load(new ServerApi(this));

    }

    public LuaValue execute(ServerCommandSource source) {
        try {
            this.source = FunctionCommand.createFunctionCommandSource(source);
            return this.script.call();
        } catch (LuaError err) {
            Luafy.LOGGER.error("[LUA: INTERPRETATION] \n" + err.getMessage());
        }
        return LuaValue.NIL;
    }



}
