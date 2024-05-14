package dev.diamond.luafy.lua.api;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.diamond.luafy.lua.LuaScript;
import dev.diamond.luafy.lua.LuafyLua;
import net.minecraft.server.command.ServerCommandSource;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.util.concurrent.atomic.AtomicInteger;

public class CommandApi extends AbstractApi {

    private final LuaScript script;

    public CommandApi(LuaScript script) {
        super("command");
        this.script = script;
    }

    @Override
    public void create(LuaTable table) {
        table.set("execute", new ExecuteFunc());
        table.set("execute_preparsed", new ExecutePreparsedFunc());
        table.set("parse", new ParseFunc());
    }

    public class ExecuteFunc extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            var c = parseCommand(arg.checkjstring());
            return LuaValue.valueOf(executeCommand(c));
        }
    }

    public class ExecutePreparsedFunc extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            LuafyLua.HexId hexid = ((LuafyLua.HexId)arg);
            var p = LuafyLua.ScriptManagements.PREPARSED_COMMANDS_CACHE.get(hexid);
            return LuaValue.valueOf(executeCommand(p));
        }
    }

    public class ParseFunc extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            LuafyLua.HexId hexId = LuafyLua.HexId.makeNewUnique(LuafyLua.ScriptManagements.PREPARSED_COMMANDS_CACHE.keySet());
            var p = parseCommand(arg.checkjstring());
            LuafyLua.ScriptManagements.PREPARSED_COMMANDS_CACHE.put(hexId, p);
            return hexId;
        }
    }

    private ParseResults<ServerCommandSource> parseCommand(String command) {
        return script.source.getDispatcher().parse(command, script.source);
    }
    private int executeCommand(ParseResults<ServerCommandSource> command) {
        try {
            AtomicInteger r = new AtomicInteger();
            script.source.getDispatcher().setConsumer((context, success, result) -> {
                r.set(result);
            });
            script.source.getDispatcher().execute(command);
            return r.get();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
