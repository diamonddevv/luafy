package dev.diamond.luafy.script.api.obj.minecraft.block;

import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import net.minecraft.state.property.Property;

import java.util.HashMap;

public class BlockStatePropertyScriptObject implements IScriptObject {

    private final Property<?> property;

    public BlockStatePropertyScriptObject(Property<?> property) {
        this.property = property;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        set.put("get_name", args -> property.getName());
    }

    public Property<?> get() {
        return property;
    }
}
