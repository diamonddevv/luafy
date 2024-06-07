package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.api.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.BaseValueConversions;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.nbt.OptionallyExplicitNbtElement;
import dev.diamond.luafy.script.api.obj.util.StorageScriptObject;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class StorageApi extends AbstractScriptApi {
    public StorageApi(AbstractScript<?> script) {
        super(script, "storage");
    }

    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        HashMap<String, AdaptableFunction> f = new HashMap<>();

        f.put("read", args -> readStorage(args, false));

        f.put("read_implicit", args -> readStorage(args, true));

        f.put("write", args -> {
            writeStorage(args);
            return null;
        });

        f.put("has", args -> {
            var id = args[0];
            var address = args[1];
            var store = getDataStorage(script.source, id);
            return store.get(address.asString()) != null;
        });

        f.put("object", args -> {
            String[] splits = args[0].asString().split(":");
            Identifier i = new Identifier(splits[0], splits[1]);
            return new StorageScriptObject(script.source, i);
        });


        return f;
    }


    // functions
    public OptionallyExplicitNbtElement readStorage(AbstractBaseValue<?, ?>[] params, boolean implicit) {
        AbstractBaseValue<?, ?> arg_id = params[0];
        AbstractBaseValue<?, ?> arg_path = params[1];

        AbstractBaseValue<?, ?> arg_type = null;
        if (!implicit) {
            arg_type = params[2];
        }

        NbtCompound cmpnd = getDataStorage(script.source, arg_id);
        NbtElement element = cmpnd.get(arg_path.asString());


        if (arg_type == null) return new OptionallyExplicitNbtElement(null, element);
        else return new OptionallyExplicitNbtElement(ScriptManager.ExplicitType.get(arg_type.asString()), element);
    }

    public void writeStorage(AbstractBaseValue<?, ?>[] params) {
        AbstractBaseValue<?, ?> arg_id   = params[0];
        AbstractBaseValue<?, ?> arg_path = params[1];
        AbstractBaseValue<?, ?> arg_data = params[2];


        NbtCompound cmpnd = getDataStorage(script.source, arg_id);
        BaseValueConversions.implicit_putBaseToNbt(cmpnd, arg_path.asString(), arg_data);
        writeDataStorage(script.source, arg_id, cmpnd);
    }

    public static DataCommandStorage getStorage(ServerCommandSource source) {
        return source.getServer().getDataCommandStorage();
    }
    public static NbtCompound getDataStorage(ServerCommandSource source, AbstractBaseValue<?, ?> id) {
        String[] splits = id.asString().split(":");
        Identifier i = new Identifier(splits[0], splits[1]);
        return getStorage(source).get(i);
    }
    public static void writeDataStorage(ServerCommandSource source, AbstractBaseValue<?, ?> id, NbtCompound compound) {
        String[] splits = id.asString().split(":");
        Identifier i = new Identifier(splits[0], splits[1]);
        getStorage(source).set(i, compound);
    }
}
