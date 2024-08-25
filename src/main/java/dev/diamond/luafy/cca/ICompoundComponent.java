package dev.diamond.luafy.cca;

import net.minecraft.nbt.NbtCompound;
import org.ladysnake.cca.api.v3.component.Component;

public interface ICompoundComponent extends Component {
    NbtCompound get();
    void set(NbtCompound nbt);
}
