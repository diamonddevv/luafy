package dev.diamond.luafy.lua.object;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.diamond.luafy.lua.LuaTypeConversions;
import net.minecraft.server.network.ServerPlayerEntity;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;


public class PlayerLuaObject extends LivingEntityLuaObject {


    public final ServerPlayerEntity player;

    public PlayerLuaObject(ServerPlayerEntity spe) {
        super(spe);
        this.player = spe;
    }
    
    @Override
    public void create() {
        super.create();
        set("dump_statistics", new DumpStatsFunc());
        set("get_ender_chest_items", new GetEnderItems());
    }

    public class DumpStatsFunc extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            var handler = player.getStatHandler();

            String s = handler.asString();
            JsonObject obj = new Gson().fromJson(s, JsonObject.class);
            return LuaTypeConversions.jsonObjToLuaTable(obj);
        }
    }

    private class GetEnderItems extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            var enderchest = player.getEnderChestInventory().heldStacks;
            ItemStackLuaObject[] arr = new ItemStackLuaObject[enderchest.size()];
            int i = 0;
            for (var stack : enderchest) { arr[i] = new ItemStackLuaObject(stack); i++; }
            return LuaTable.listOf(arr);
        }
    }
}
