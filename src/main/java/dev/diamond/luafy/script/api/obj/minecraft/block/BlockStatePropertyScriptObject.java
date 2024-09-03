package dev.diamond.luafy.script.api.obj.minecraft.block;

import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import net.minecraft.state.property.Property;

public class BlockStatePropertyScriptObject extends AbstractTypedScriptObject<Property<?>> {

    private final Property<?> property;

    public BlockStatePropertyScriptObject(Property<?> property) {
        this.property = property;
    }

    public Property<?> get() {
        return property;
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {
        f.add_NoParams_Desc("get_name", args -> property.getName(), "Returns the name of this property.", String.class);
    }

    @Override
    public String getName() {
        return "BlockStateProperty";
    }
}
