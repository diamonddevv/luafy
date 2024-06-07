package dev.diamond.luafy.script.api.obj.entity.display;

import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.api.obj.minecraft.item.ItemStackScriptObject;
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class ItemDisplayEntityScriptObject extends DisplayEntityScriptObject {

    private final ItemDisplayEntity itemDisplay;

    public ItemDisplayEntityScriptObject(ItemDisplayEntity entity) {
        super(entity);
        this.itemDisplay = entity;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        super.addFunctions(set);

        set.put("set_stack", args -> {
            ItemStack stack = args[0].asScriptObjectAssertive(ItemStackScriptObject.class).get();
            itemDisplay.getStackReference(0).set(stack);
            return null;
        });
    }
}
