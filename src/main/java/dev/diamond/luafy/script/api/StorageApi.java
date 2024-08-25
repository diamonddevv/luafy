package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.BaseValueConversions;
import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.api.obj.util.StorageScriptObject;
import dev.diamond.luafy.script.nbt.OptionallyExplicitNbtElement;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public class StorageApi extends AbstractTypedScriptApi {
    public StorageApi(AbstractScript<?> script) {
        super(script, "storage");
    }


    @Override
    public void getTypedFunctions(TypedFunctionList f) {

        f.add_Desc("read", args -> readStorage(args, false), "Reads the NBT storage at the id supplied explicitly as the specified type. Returns a value typed dependent on the 'type' param.",
                Object.class,
                new NamedParam("storageId", String.class),
                new NamedParam("storageTag", String.class),
                new NamedParam("type", "The explicit type to read the data as. One of: STR, NUM, LIST, OBJ, BOOL.", String.class)
                );

        f.add_Desc("read_implicit", args -> readStorage(args, true), "Reads the NBT storage at the id supplied implicitly. Type is determined at runtime.",
                Object.class,
                new NamedParam("storageId", String.class),
                new NamedParam("storageTag", String.class)
        );

        f.add_Void_Desc("write", args -> {
            writeStorage(args);
            return null;
        }, "Writes the value given to the specified NBT Storage with the key supplied.",
                new NamedParam("storageId", String.class),
                new NamedParam("storageTag", String.class),
                new NamedParam("value", Object.class)
        );

        f.add_Desc("has", args -> {
                    var id = args[0];
                    var address = args[1];
                    var store = getDataStorage(script.source, id);
                    return store.get(address.asString()) != null;
                }, "Returns true if the specified NBT storage has a value at the key supplied.",
                Boolean.class,
                new NamedParam("storageId", String.class),
                new NamedParam("storageTag", String.class)
        );

        f.add_Desc("object", args -> {
                    String[] splits = args[0].asString().split(":");
                    Identifier i = Identifier.of(splits[0], splits[1]);
                    return new StorageScriptObject(script.source, i);
                }, "Returns an object wrapping around the supplied storage.",
                StorageScriptObject.class,
                new NamedParam("storageId", String.class)
        );
    }

    @Override
    public String getDescription() {
        return "Provides functions relating to modifying NBT storage data.";
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
        Identifier i = Identifier.of(splits[0], splits[1]);
        return getStorage(source).get(i);
    }
    public static void writeDataStorage(ServerCommandSource source, AbstractBaseValue<?, ?> id, NbtCompound compound) {
        String[] splits = id.asString().split(":");
        Identifier i = Identifier.of(splits[0], splits[1]);
        getStorage(source).set(i, compound);
    }
}
