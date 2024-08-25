package dev.diamond.luafy.script.api.obj.minecraft.item;

import dev.diamond.luafy.script.abstraction.BaseValueConversions;
import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ComponentMapScriptObject extends AbstractTypedScriptObject<ComponentMap> {

    private static final NamedParam COMPONENT_ID = new NamedParam("component_id", String.class);

    private final ComponentMap map;

    public ComponentMapScriptObject(ComponentMap map) {
        this.map = map;
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {
        f.add_Desc("get_custom_data_nbt", args -> {
            NbtComponent nbtComponent = map.get(DataComponentTypes.CUSTOM_DATA);

            NbtCompound compound;
            if (nbtComponent == null) {
                compound = new NbtCompound();
            } else {
                compound = nbtComponent.copyNbt();
            }

            return BaseValueConversions.implicit_nbtToBase(compound, args[0]::adapt);
        }, "Gets the value of the component `minecraft:custom_data`, returned as a Map/Dictionary language equivalent.", AbstractBaseValue.class);
    }

    @Override
    public ComponentMap get() {
        return map;
    }

    public ComponentType<?> getComponentFromId(Identifier id) {
        return Registries.DATA_COMPONENT_TYPE.get(id);
    }
}
