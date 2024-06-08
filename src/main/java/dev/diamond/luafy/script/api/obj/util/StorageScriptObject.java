package dev.diamond.luafy.script.api.obj.util;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.BaseValueConversions;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import dev.diamond.luafy.script.api.StorageApi;
import dev.diamond.luafy.script.nbt.OptionallyExplicitNbtElement;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public class StorageScriptObject extends AbstractTypedScriptObject<NbtCompound> {

    private final ServerCommandSource source;
    private final Identifier id;

    public StorageScriptObject(ServerCommandSource source, Identifier id) {
        this.source = source;
        this.id = id;
    }

    public NbtCompound get() {
        return StorageApi.getStorage(source).get(id);
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {
        f.add(
                "read", args ->
                        new OptionallyExplicitNbtElement(ScriptManager.ExplicitType.get(args[1].asString()), get().get(args[0].asString())),
                NbtElement.class,
                new NamedParam("address", String.class), new NamedParam("type", String.class)
        );


        f.add_Void(
                "write", args ->{
                    var address = args[0];
                    var data = args[1];
                    BaseValueConversions.implicit_putBaseToNbt(get(), address.asString(), data);
                    return null;
                },
                new NamedParam("address", String.class),
                new NamedParam("data", Object.class)
        );

        f.add("has",
                args -> {
                    var address = args[0];
                    return get().get(address.asString()) != null;
                }, Boolean.class,
                new NamedParam("address", String.class));
    }
}
