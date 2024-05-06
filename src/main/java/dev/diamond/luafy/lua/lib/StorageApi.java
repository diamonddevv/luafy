package dev.diamond.luafy.lua.lib;

import dev.diamond.luafy.lua.ArrArgFunction;
import dev.diamond.luafy.lua.LuaScriptManager;
import dev.diamond.luafy.lua.LuaTypeConversions;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class StorageApi extends AbstractLib {

    private final LuaScriptManager script;

    public StorageApi(LuaScriptManager script) {
        super("storage");
        this.script = script;
    }

    @Override
    public void create(LuaTable table) {
        table.set("read_implicit", new StorageApi.ReadImplicitFunc());
        table.set("read", new StorageApi.ReadFunc());
        table.set("write", new StorageApi.WriteFunc());
        table.set("has", new StorageApi.HasFunc());
    }


    public class ReadImplicitFunc extends ArrArgFunction {
        @Override
        public LuaValue call(LuaValue[] params) {
            return readStorage(params, true);
        }
    }

    public class ReadFunc extends ArrArgFunction {
        @Override
        public LuaValue call(LuaValue[] params) {
            return readStorage(params, false);
        }
    }

    public class WriteFunc extends ArrArgFunction {
        @Override
        public LuaValue call(LuaValue[] params) {
            writeStorage(params, true);
            return NIL;
        }
    }

    public class HasFunc extends ArrArgFunction {
        @Override
        public LuaValue call(LuaValue[] params) {
            LuaValue arg_id = params[0];
            LuaValue arg_address = params[1];

            NbtCompound cmpnd = getDataStorage(script.source, arg_id);
            return LuaValue.valueOf(cmpnd.contains(arg_address.tojstring()));
        }
    }



    // Global Executions
    private LuaValue readStorage(LuaValue[] params, boolean implicit) {
        LuaValue arg_id = params[0];
        LuaValue arg_path = params[1];

        LuaValue arg_type = null;
        if (!implicit) {
            arg_type = params[2];
        }

        NbtCompound cmpnd = getDataStorage(script.source, arg_id);

        if (arg_type == null) return LuaTypeConversions.implicitNbtToLua(cmpnd, arg_path.tojstring());
        else return LuaTypeConversions.explicitNbtToLua(cmpnd, arg_path.tojstring(), arg_type.tojstring());
    }

    public void writeStorage(LuaValue[] params, boolean implicit) {
        LuaValue arg_id = params[0];
        LuaValue arg_path = params[1];
        LuaValue arg_data = params[2];

        LuaValue arg_type = null;
        if (!implicit) {
            arg_type = params[3];
        }

        NbtCompound cmpnd = getDataStorage(script.source, arg_id);

        if (arg_type == null) LuaTypeConversions.nbtPutObject(cmpnd, arg_path.tojstring(), LuaTypeConversions.luaToObj(arg_data));
        else LuaTypeConversions.nbtPutObject(cmpnd, arg_path.tojstring(), LuaTypeConversions.luaToObj(arg_data), arg_type.tojstring());

        writeDataStorage(script.source, arg_id, cmpnd);
    }

    private static DataCommandStorage getStorage(ServerCommandSource source) {
        return source.getServer().getDataCommandStorage();
    }


    public static NbtCompound getDataStorage(ServerCommandSource source, LuaValue id) {
        String[] splits = id.tojstring().split(":");
        Identifier i = new Identifier(splits[0], splits[1]);
        return getStorage(source).get(i);
    }

    public static void writeDataStorage(ServerCommandSource source, LuaValue id, NbtCompound compound) {
        String[] splits = id.tojstring().split(":");
        Identifier i = new Identifier(splits[0], splits[1]);
        getStorage(source).set(i, compound);
    }
}
