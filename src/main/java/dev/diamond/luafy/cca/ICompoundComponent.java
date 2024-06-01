package dev.diamond.luafy.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;

public interface ICompoundComponent extends Component {
    NbtCompound get();
    void set(NbtCompound nbt);
}
