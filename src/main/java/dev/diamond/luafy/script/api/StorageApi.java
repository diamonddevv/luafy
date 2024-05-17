package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.lua.LuaTypeConversions;
import dev.diamond.luafy.script.nbt.OptionallyExplicitNbtElement;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class StorageApi extends AbstractScriptApi {
    public StorageApi(AbstractScript<?, ?> script) {
        super(script, "storage");
    }

    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        HashMap<String, AdaptableFunction> f = new HashMap<>();

        f.put("read", args -> {

        });

        f.put("read_implicit", args -> {

        });

        f.put("write", args -> {

        });

        f.put("has", args -> {

        });

        f.put("object", args -> {

        });


        return f;
    }


    // functions
    public OptionallyExplicitNbtElement readStorage(AbstractBaseValue<?, ?, ?>[] params, boolean implicit) {
        AbstractBaseValue<?, ?, ?> arg_id = params[0];
        AbstractBaseValue<?, ?, ?> arg_path = params[1];

        AbstractBaseValue<?, ?, ?> arg_type = null;
        if (!implicit) {
            arg_type = params[2];
        }

        NbtCompound cmpnd = getDataStorage(script.source, arg_id);
        NbtElement element = cmpnd.get(arg_path.asString());


        if (arg_type == null) return new OptionallyExplicitNbtElement(null, element);
        else return new OptionallyExplicitNbtElement(ScriptManager.ExplicitType.get(arg_type.asString()), element);
    }

    public void writeStorage(AbstractBaseValue<?, ?, ?>[] params, boolean implicit) {
        AbstractBaseValue<?, ?, ?> arg_id = params[0];
        AbstractBaseValue<?, ?, ?> arg_path = params[1];
        AbstractBaseValue<?, ?, ?> arg_data = params[2];

        AbstractBaseValue<?, ?, ?> arg_type = null;
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
    public static NbtCompound getDataStorage(ServerCommandSource source, AbstractBaseValue<?, ?, ?> id) {
        String[] splits = id.asString().split(":");
        Identifier i = new Identifier(splits[0], splits[1]);
        return getStorage(source).get(i);
    }
    public static void writeDataStorage(ServerCommandSource source, AbstractBaseValue<?, ?, ?> id, NbtCompound compound) {
        String[] splits = id.asString().split(":");
        Identifier i = new Identifier(splits[0], splits[1]);
        getStorage(source).set(i, compound);
    }
}
