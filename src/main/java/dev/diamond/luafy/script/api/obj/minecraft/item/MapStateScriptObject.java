package dev.diamond.luafy.script.api.obj.minecraft.item;

import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import dev.diamond.luafy.script.api.obj.entity.PlayerEntityScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.WorldScriptObject;
import dev.diamond.luafy.script.api.obj.util.BufferedImageScriptObject;
import dev.diamond.luafy.util.LuafyUtil;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class MapStateScriptObject implements IScriptObject<MapState> {

    public static final int MAX_DIMENSION = 128;

    private final MapState map;
    private int id;

    public MapStateScriptObject(MapState map) {
        this.map = map;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        set.put("set_image", args -> {
            BufferedImage img = args[0].asScriptObjectAssertive(BufferedImageScriptObject.class).get();
            ServerWorld world = args[1].asScriptObjectAssertive(WorldScriptObject.class).get();

            for (int y = 0; y < img.getHeight(); y++)
                for (int x = 0; x < img.getWidth(); x++) {
                    LuafyUtil.ClosestMapColor color = LuafyUtil.getClosestMapColor(new Color(img.getRGB(x, y), true));
                    map.setColor(x, y, color.getColor().getRenderColorByte(color.getBrightnessLevel()));
                }

            int i = world.getNextMapId();
            world.putMapState(FilledMapItem.getMapName(i), map);

            id = i;

            return null;
        });

        set.put("set_map_item", args -> {
            ServerPlayerEntity e = args[0].asScriptObjectAssertive(PlayerEntityScriptObject.class).player;
            ItemStack stack = new ItemStack(Items.FILLED_MAP, 1);

            if (id == -1) return null;

            if (stack.getItem() instanceof FilledMapItem m) {
                FilledMapItem.setMapId(stack, id);
                e.setStackInHand(Hand.MAIN_HAND, stack);
            }

            return null;
        });
    }

    @Override
    public MapState get() {
        return map;
    }
}
