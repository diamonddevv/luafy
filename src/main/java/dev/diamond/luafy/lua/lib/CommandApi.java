package dev.diamond.luafy.lua.lib;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.diamond.luafy.lua.LuaScriptManager;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.util.concurrent.atomic.AtomicInteger;

public class CommandApi extends AbstractLib {

    private final LuaScriptManager script;

    public CommandApi(LuaScriptManager script) {
        super("command");
        this.script = script;
    }

    @Override
    public void create(LuaTable table) {
        table.set("execute", new CommandApi.ExecuteFunc());
    }

    public class ExecuteFunc extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            try {
                AtomicInteger r = new AtomicInteger();
                script.source.getDispatcher().setConsumer((context, success, result) -> {
                    r.set(result);
                });
                script.source.getDispatcher().execute(arg.toString(), script.source);
                return LuaValue.valueOf(r.get());
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
