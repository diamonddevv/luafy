package dev.diamond.luafy.script.api.obj.minecraft.block;

import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;

public class BlockStateScriptObject extends AbstractTypedScriptObject<BlockState> {

    private final BlockState block;

    public BlockStateScriptObject(BlockState block) {
        this.block = block;
    }
    @Override
    public BlockState get() {
        return block;
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {

        f.add_NoParams_Desc("get_block_id", args -> Registries.BLOCK.getId(block.getBlock()).toString(), "Gets the registry id of this block.", String.class);
        f.add_NoParams_Desc("get_properties", args -> block.getProperties().stream().map(BlockStatePropertyScriptObject::new).toList(), "Returns a list of BlockStateProperties attached to this block.", List.class);

        f.add_Desc("get_property_value", args -> block.get(args[0].asScriptObjectAssertive(BlockStatePropertyScriptObject.class).get()), "Gets the value of the property supplied on this block.", Object.class, new NamedParam("property", BlockStatePropertyScriptObject.class));
        f.add_Desc("in_tag", args -> block.isIn(TagKey.of(RegistryKeys.BLOCK, new Identifier(args[0].asString()))), "Returns true if this block is in the specified block tag.", Boolean.class, new NamedParam("tagId", String.class));
    }
}
