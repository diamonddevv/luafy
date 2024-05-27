package dev.diamond.luafy.script.api.obj.util;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.BaseValueConversions;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import dev.diamond.luafy.script.api.StorageApi;
import dev.diamond.luafy.script.nbt.OptionallyExplicitNbtElement;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class StorageScriptObject implements IScriptObject {

    private final ServerCommandSource source;
    private final Identifier id;

    public StorageScriptObject(ServerCommandSource source, Identifier id) {
        this.source = source;
        this.id = id;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        set.put("read", args ->
                new OptionallyExplicitNbtElement(ScriptManager.ExplicitType.get(args[1].asString()), get().get(args[0].asString())));

        set.put("write", args -> {
            var address = args[0];
            var data = args[1];
            BaseValueConversions.implicit_putBaseToNbt(get(), address.asString(), data);
            return null;
        });

        set.put("has", args -> {
            var address = args[0];
            return get().get(address.asString()) != null;
        });
    }

    private NbtCompound get() {
        return StorageApi.getStorage(source).get(id);
    }
}
