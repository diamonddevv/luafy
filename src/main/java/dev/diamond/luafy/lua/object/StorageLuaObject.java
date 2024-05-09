package dev.diamond.luafy.lua.object;

import dev.diamond.luafy.lua.LuaTypeConversions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class StorageLuaObject extends AbstractLuaObject {

    private final ServerCommandSource source;
    private final Identifier id;

    public StorageLuaObject(ServerCommandSource source, Identifier id) {
        this.source = source;
        this.id = id;
    }

    @Override
    public void create() {
        set("read", new ObjReadFunc());
        set("write", new ObjWriteFunc());
        set("has", new ObjHasFunc());
    }

    private class ObjReadFunc extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            String address = arg1.checkjstring();
            String type = arg2.checkjstring();

            return LuaTypeConversions.explicitNbtToLua(source.getServer().getDataCommandStorage().get(id).get(address), type);
        }
    }

    private class ObjWriteFunc extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            String address = arg1.checkjstring();
            Object data = LuaTypeConversions.luaToObj(arg2);

            NbtCompound compound = source.getServer().getDataCommandStorage().get(id);
            LuaTypeConversions.implicitNbtPutObject(compound, address, data);
            source.getServer().getDataCommandStorage().set(id, compound);

            return NIL;
        }
    }

    private class ObjHasFunc extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            NbtCompound cmpnd = source.getServer().getDataCommandStorage().get(id);
            return LuaValue.valueOf(cmpnd.contains(arg.tojstring()));
        }
    }
}
