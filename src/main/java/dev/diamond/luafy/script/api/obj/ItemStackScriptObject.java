package dev.diamond.luafy.script.api.obj;

import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.BaseValueConversions;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import dev.diamond.luafy.script.nbt.OptionallyExplicitNbtElement;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.HashMap;

public class ItemStackScriptObject implements IScriptObject {
    private final ItemStack stack;

    public ItemStackScriptObject(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        set.put("get_id", args -> Registries.ITEM.getId(stack.getItem()).toString());
        set.put("get_count", args -> stack.getCount());

        set.put("get_nbt", args -> new OptionallyExplicitNbtElement(null, stack.getOrCreateNbt()));
        set.put("set_nbt", args -> {
            stack.setNbt(BaseValueConversions.mapToCompound((HashMap<AbstractBaseValue<?,?>, AbstractBaseValue<?,?>>) args[0].asMap()));
            return null;
        });
    }


    public ItemStack get() {
        return stack;
    }
}
