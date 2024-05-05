package dev.diamond.luafy.lua;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.diamond.luafy.Luafy;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LuaScriptManager {

    private final String scriptString;
    private final Globals scriptGlobals;
    private final LuaValue script;

    private ServerCommandSource source;

    public LuaScriptManager(String scriptString) {
        this.scriptString = scriptString;

        this.scriptGlobals = JsePlatform.standardGlobals();
        loadFunctions();
        LuaC.install(this.scriptGlobals);
        this.script = this.scriptGlobals.load(scriptString);

    }

    private void loadFunctions() {
        // libs

        // command function -> executes command on server command source
        addGlobal("command", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                try {
                    AtomicInteger r = new AtomicInteger();
                    source.getDispatcher().setConsumer((context, success, result) -> {
                        r.set(result);
                    });
                    source.getDispatcher().execute(arg.toString(), source);
                    return LuaValue.valueOf(r.get());
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // data storage query
        addGlobal("storage", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                DataCommandStorage storage = source.getServer().getDataCommandStorage();
                String[] splits = arg1.tojstring().split(":");
                NbtCompound cmpnd = storage.get(new Identifier(splits[0], splits[1]));
                return LuaValue.valueOf(cmpnd.getString(arg2.tojstring()));
            }
        });
    }

    public LuaValue execute(ServerCommandSource source) {
        try {
            this.source = FunctionCommand.createFunctionCommandSource(source);
            return this.script.call();
        } catch (LuaError err) {
            Luafy.LOGGER.error("[LUA: INTERPRETATION] " + err.getMessage());
        }
        return LuaValue.NIL;
    }

    private void addGlobal(String name, Object obj) {
        scriptGlobals.set(name, javaToLua(obj).arg1());
    }

    // all below is yoinked from figura !! some is edited though

    private static Varargs javaToLua(Object val) {
        if (val == null)
            return LuaValue.NIL;
        else if (val instanceof LuaValue l)
            return l;
        else if (val instanceof Double d)
            return LuaValue.valueOf(d);
        else if (val instanceof String s)
            return LuaValue.valueOf(s);
        else if (val instanceof Boolean b)
            return LuaValue.valueOf(b);
        else if (val instanceof Integer i)
            return LuaValue.valueOf(i);
        else if (val instanceof Float f)
            return LuaValue.valueOf(f);
        else if (val instanceof Byte b)
            return LuaValue.valueOf(b);
        else if (val instanceof Long l)
            return LuaValue.valueOf(l);
        else if (val instanceof Character c)
            return LuaValue.valueOf(c);
        else if (val instanceof Short s)
            return LuaValue.valueOf(s);
        else if (val instanceof Collection<?> collection)
            return wrapArray(collection.toArray());
        else if (val.getClass().isArray())
            return wrapArray(val);
        else {
            Luafy.LOGGER.error("Forbidden Lua Type (" + val.getClass() + ")");
            return LuaValue.NIL;
        }
    }
    private static Varargs wrapArray(Object array) {
        int len = Array.getLength(array);
        LuaValue[] args = new LuaValue[len];

        for (int i = 0; i < len; i++)
            args[i] = javaToLua(Array.get(array, i)).arg1();

        return LuaValue.varargsOf(args);
    }
}
