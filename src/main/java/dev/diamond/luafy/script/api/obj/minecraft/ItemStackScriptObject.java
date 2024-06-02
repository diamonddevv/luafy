package dev.diamond.luafy.script.api.obj.minecraft;

import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.BaseValueConversions;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import dev.diamond.luafy.script.api.obj.entity.EntityScriptObject;
import dev.diamond.luafy.script.api.obj.entity.LivingEntityScriptObject;
import dev.diamond.luafy.script.api.obj.entity.PlayerEntityScriptObject;
import dev.diamond.luafy.script.nbt.OptionallyExplicitNbtElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;

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

        set.put("set_cooldown_ticks", args -> {
            ServerPlayerEntity player = args[0].asScriptObjectAssertive(PlayerEntityScriptObject.class).player;
            int ticks = args[1].asInt();
            player.getItemCooldownManager().set(stack.getItem(), ticks);
            return null;
        });

        set.put("get_cooldown_ticks", args -> {
            ServerPlayerEntity player = args[0].asScriptObjectAssertive(PlayerEntityScriptObject.class).player;
            return player.getItemCooldownManager().getCooldownProgress(stack.getItem(), 0f);
        });

        set.put("remove_cooldown", args -> {
            ServerPlayerEntity player = args[0].asScriptObjectAssertive(PlayerEntityScriptObject.class).player;
            player.getItemCooldownManager().remove(stack.getItem());
            return null;
        });
    }


    public ItemStack get() {
        return stack;
    }
}
