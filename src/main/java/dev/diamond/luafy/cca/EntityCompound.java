package dev.diamond.luafy.cca;

import net.minecraft.nbt.NbtCompound;

public class EntityCompound implements ICompoundComponent {

    private final String key;
    private NbtCompound nbt;

    public EntityCompound(String key, NbtCompound nbt) {
        this.key = key;
        this.nbt = nbt;
    }

    @Override public NbtCompound get() {
        return nbt;
    }
    @Override public void set(NbtCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        nbt = tag.getCompound(key);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.put(key, nbt);
    }
}
