package dev.diamond.luafy.script.api.obj.minecraft.item;

import com.mojang.serialization.DataResult;
import dev.diamond.luafy.script.abstraction.BaseValueConversions;
import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ComponentMapScriptObject extends AbstractTypedScriptObject<ComponentMap> {

    private static final NamedParam COMPONENT_ID = new NamedParam("component_id", Identifier.class);

    private final ComponentMap map;

    public ComponentMapScriptObject(ComponentMap map) {
        this.map = map;
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {
        f.add_Desc("get_nbt_component", args -> {
            Identifier id = Identifier.of(args[0].asString());
            NbtComponent nbtComponent = map.get(getComponentFromId(id, NbtComponent.class));

            NbtCompound compound;
            if (nbtComponent == null) {
                compound = new NbtCompound();
            } else {
                compound = nbtComponent.copyNbt();
            }

            return BaseValueConversions.implicit_nbtToBase(compound, args[0]::adapt);
        }, "Gets the NBT value of the specified component.", AbstractBaseValue.class, COMPONENT_ID);


        f.add_Desc("get_int_component", args -> {
            Identifier id = Identifier.of(args[0].asString());

            return  map.get(getComponentFromId(id, Integer.class));
        }, "Gets the int value of the specified component.", Integer.class, COMPONENT_ID);


        f.add_Desc("get_component", args -> {
            Identifier id = Identifier.of(args[0].asString());
            ComponentType<Object> component = getComponentFromId(id, Object.class);
            DataResult<NbtElement> res = component.getCodec().encodeStart(NbtOps.INSTANCE, map.get(component));

            return BaseValueConversions.implicit_nbtToBase(res.getOrThrow(), args[0]::adapt);

        }, "Gets the value of the specified component using the internal codec, as NBT in a Map/Dictionary language equivalent.", AbstractBaseValue.class, COMPONENT_ID);
    }

    @Override
    public ComponentMap get() {
        return map;
    }

    public <T> ComponentType<T> getComponentFromId(Identifier id, Class<T> componentType) {
        return (ComponentType<T>) getComponentFromId(id);
    }
    public ComponentType<?> getComponentFromId(Identifier id) {
        return Registries.DATA_COMPONENT_TYPE.get(id);
    }

    @Override
    public String getName() {
        return "ComponentMap";
    }
}
