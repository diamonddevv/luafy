package dev.diamond.luafy.script.api.obj;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class PlayerEntityScriptObject extends LivingEntityScriptObject {

    private final ServerPlayerEntity player;

    public PlayerEntityScriptObject(ServerPlayerEntity entity) {
        super(entity);
        this.player = entity;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        super.addFunctions(set);

        set.put("dump_statistics", args -> dumpStats());
        set.put("get_ender_items", args -> getEnderItems());
    }

    private JsonObject dumpStats() {
        var handler = player.getStatHandler();
        String s = handler.asString();
        return new Gson().fromJson(s, JsonObject.class);
    }

    private Collection<ItemStackScriptObject> getEnderItems() {
        var enderchest = player.getEnderChestInventory().heldStacks;
        ItemStackScriptObject[] arr = new ItemStackScriptObject[enderchest.size()];
        int i = 0;
        for (var stack : enderchest) { arr[i] = new ItemStackScriptObject(stack); i++; }
        return Arrays.stream(arr).toList();
    }
}
