package dev.diamond.luafy.script.nbt;

import dev.diamond.luafy.script.ScriptManager;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;

public record OptionallyExplicitNbtElement(@Nullable ScriptManager.ExplicitType type, NbtElement nbt) {
    public boolean isExplicit() {
        return type != null;
    }
}
