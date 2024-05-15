package dev.diamond.luafy.script.old.api;

import dev.diamond.luafy.script.old.ArrArgFunction;
import dev.diamond.luafy.script.old.Old_LuaScript;
import dev.diamond.luafy.script.old.LuaTypeConversions;
import dev.diamond.luafy.script.old.object.StorageLuaObject;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class StorageApi extends AbstractApi {

    private final Old_LuaScript script;

    public StorageApi(Old_LuaScript script) {
        super("storage");
        this.script = script;
    }

    @Override
    public void create(LuaTable table) {
        table.set("read_implicit", new ReadImplicitFunc());
        table.set("read", new ReadFunc());
        table.set("write", new WriteFunc());
        table.set("has", new HasFunc());

        table.set("object", new GetObjectFunc());
    }


    private class ReadImplicitFunc extends ArrArgFunction {
        @Override
        public LuaValue call(LuaValue[] params) {
            return readStorage(params, true);
        }
    }
    private class ReadFunc extends ArrArgFunction {
        @Override
        public LuaValue call(LuaValue[] params) {
            return readStorage(params, false);
        }
    }
    private class WriteFunc extends ArrArgFunction {
        @Override
        public LuaValue call(LuaValue[] params) {
            writeStorage(params, true);
            return NIL;
        }
    }
    private class HasFunc extends ArrArgFunction {
        @Override
        public LuaValue call(LuaValue[] params) {
            LuaValue arg_id = params[0];
            LuaValue arg_address = params[1];

            NbtCompound cmpnd = getDataStorage(script.source, arg_id);
            return LuaValue.valueOf(cmpnd.contains(arg_address.tojstring()));
        }
    }

    private class GetObjectFunc extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            String[] splits = arg.checkjstring().split(":");
            Identifier i = new Identifier(splits[0], splits[1]);
            return new StorageLuaObject(script.source, i);
        }
    }


    // Global Executions
    public LuaValue readStorage(LuaValue[] params, boolean implicit) {
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

        if (arg_type == null) LuaTypeConversions.implicitNbtPutObject(cmpnd, arg_path.tojstring(), LuaTypeConversions.luaToObj(arg_data));
        else LuaTypeConversions.explicitNbtPutObject(cmpnd, arg_path.tojstring(), LuaTypeConversions.luaToObj(arg_data), arg_type.tojstring());

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
