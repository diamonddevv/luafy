package dev.diamond.luafy.lua;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.lua.api.CommandApi;
import dev.diamond.luafy.lua.api.ContextApi;
import dev.diamond.luafy.lua.api.ServerApi;
import dev.diamond.luafy.lua.api.StorageApi;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaScript {

    private final String scriptString;
    private final Globals scriptGlobals;
    private final LuaValue script;

    public ServerCommandSource source;
    public LuaTable context;
    public LuaTable outContext;

    public LuaScript(String scriptString) {
        this.scriptString = scriptString;

        this.scriptGlobals = JsePlatform.standardGlobals();
        loadFunctions();
        LuaC.install(this.scriptGlobals);
        this.script = this.scriptGlobals.load(scriptString);

    }

    private void loadFunctions() {
        // gloabal libs
        scriptGlobals.load(new CommandApi(this));
        scriptGlobals.load(new StorageApi(this));
        scriptGlobals.load(new ServerApi(this));
        scriptGlobals.load(new ContextApi(this));

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
