package dev.diamond.luafy.script.api.obj.entity;

import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.api.obj.ItemStackScriptObject;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

import java.util.HashMap;

public class LivingEntityScriptObject extends EntityScriptObject {

    private final LivingEntity living;

    public LivingEntityScriptObject(LivingEntity entity) {
        super(entity);
        this.living = entity;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        super.addFunctions(set);

        set.put("get_mainhand", args -> getStack(EquipmentSlot.MAINHAND));
        set.put("get_offhand", args -> getStack(EquipmentSlot.OFFHAND));

        set.put("get_head_stack", args -> getStack(EquipmentSlot.HEAD));
        set.put("get_chest_stack", args -> getStack(EquipmentSlot.CHEST));
        set.put("get_legs_stack", args -> getStack(EquipmentSlot.LEGS));
        set.put("get_feet_stack", args -> getStack(EquipmentSlot.FEET));


        set.put("ignite", args -> {
            living.setFireTicks(args[0].asInt());
            living.setOnFire(true);
            return null;
        });
        set.put("extinguish", args -> { living.setOnFire(false); return null; });
    }


    private ItemStackScriptObject getStack(EquipmentSlot slot) {
        return new ItemStackScriptObject(living.getEquippedStack(slot));
    }
}
