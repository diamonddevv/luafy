package dev.diamond.luafy.script.api.obj.minecraft.block;

import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class BlockStateScriptObject implements IScriptObject {

    private final BlockState block;

    public BlockStateScriptObject(BlockState block) {
        this.block = block;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        set.put("get_block_id", args -> Registries.BLOCK.getId(block.getBlock()));
        set.put("get_properties", args -> block.getProperties().stream().map(BlockStatePropertyScriptObject::new).toList());
        set.put("get_property_value", args -> block.get(args[0].asScriptObjectAssertive(BlockStatePropertyScriptObject.class).get()));

        set.put("in_tag", args -> block.isIn(TagKey.of(RegistryKeys.BLOCK, new Identifier(args[0].asString()))));
    }
}
