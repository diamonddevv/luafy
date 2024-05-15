package dev.diamond.luafy.script.old.api;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.diamond.luafy.script.old.Old_LuaScript;
import dev.diamond.luafy.script.old.LuafyLua;
import dev.diamond.luafy.util.HexId;
import net.minecraft.server.command.ServerCommandSource;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.concurrent.atomic.AtomicInteger;

public class CommandApi extends AbstractApi {

    private final Old_LuaScript script;

    public CommandApi(Old_LuaScript script) {
        super("command");
        this.script = script;
    }

    @Override
    public void create(LuaTable table) {
        table.set("execute", new ExecuteFunc());
        table.set("execute_preparsed", new ExecutePreparsedFunc());
        table.set("parse", new ParseFunc());

        //table.set("as_entity_group", new AsEntityGroupFunc()); // not sure how this works.
    }

    public class ExecuteFunc extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            var c = parseCommand(arg.checkjstring(), script.source);
            return LuaValue.valueOf(executeCommand(c, script.source));
        }
    }

    public class ExecutePreparsedFunc extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            HexId hexid = ((HexId)arg);
            var p = HexId.getHashed(LuafyLua.ScriptManagements.PREPARSED_COMMANDS_CACHE, hexid);
            return LuaValue.valueOf(executeCommand(p, script.source));
        }
    }

    public class ParseFunc extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            HexId hexId = HexId.makeNewUnique(LuafyLua.ScriptManagements.PREPARSED_COMMANDS_CACHE.keySet());
            var p = parseCommand(arg.checkjstring(), script.source);
            LuafyLua.ScriptManagements.PREPARSED_COMMANDS_CACHE.put(hexId, p);
            return hexId;
        }
    }

    public class AsEntityGroupFunc extends TwoArgFunction {

        @Override
        public LuaValue call(LuaValue arg, LuaValue arg2) {
            HexId hexid = (HexId) arg;
            var group = hexid.getHashed(LuafyLua.ScriptManagements.ENTITY_GROUP_CACHE);
            var parsed = parseCommand(arg2.checkjstring(), script.source);
            for (var entity : group) {
                executeCommand(parsed, entity.getCommandSource()
                        .withEntity(entity)
                        .withPosition(entity.getPos())
                        .withRotation(entity.getRotationClient())
                        .withLevel(script.source.level)
                );
            }

            return NIL;
        }
    }

    public static ParseResults<ServerCommandSource> parseCommand(String command, ServerCommandSource source) {
        return source.getDispatcher().parse(command, source);
    }
    public static int executeCommand(ParseResults<ServerCommandSource> command, ServerCommandSource source) {
        try {
            AtomicInteger r = new AtomicInteger();
            source.getDispatcher().setConsumer((context, success, result) -> {
                r.set(result);
            });
            source.getDispatcher().execute(command);
            return r.get();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
