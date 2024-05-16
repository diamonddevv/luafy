package dev.diamond.luafy.script.old.object;

import dev.diamond.luafy.script.lua.LuaTypeConversions;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.function.Function;

public class ItemStackLuaObject extends AbstractLuaObject {
    private final ItemStack stack;

    public ItemStackLuaObject(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void create() {
        set("get_id", new GetWithStackFunc(s -> valueOf(Registries.ITEM.getId(s.getItem()).toString())));
        set("get_count", new GetWithStackFunc(s -> valueOf(s.getCount())));

        set("get_nbt", new GetNbtTableFunc());
        set("set_nbt", new SetNbtTableFunc());
    }

    public class GetWithStackFunc extends GetFunc<ItemStack> {
        public GetWithStackFunc(Function<ItemStack, Object> func) {
            super(func);
        }

        @Override
        public ItemStack get() {
            return stack;
        }
    }


    public class GetNbtTableFunc extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            return LuaTypeConversions.tableFromNbt(stack.getOrCreateNbt());
        }
    }

    public class SetNbtTableFunc extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            stack.setNbt(LuaTypeConversions.tableToNbt(arg.checktable()));
            return NIL;
        }
    }
}
