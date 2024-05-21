package dev.diamond.luafy.mixin;

import com.mojang.brigadier.context.ParsedArgument;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ParsedArgument.class)
public interface ParsedArgumentAccessor {

    @Mutable
    @Accessor("result")
    void setResult(Object o);
}
