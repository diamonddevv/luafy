package dev.diamond.luafy.lua.object;

import dev.diamond.luafy.lua.LuaTypeConversions;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.function.Function;

public abstract class AbstractLuaObject extends LuaTable {


    public AbstractLuaObject() {
        create();
    }

    public abstract void create();


    public abstract static class GetFunc<T> extends ZeroArgFunction {

        private final Function<T, Object> func;

        public GetFunc(Function<T, Object> func) {
            this.func = func;
        }

        public abstract T get();

        @Override
        public LuaValue call() {
            Object obj = func.apply(get());
            if (obj instanceof LuaValue lv) return lv;
            return LuaTypeConversions.luaFromObj(obj);
        }
    }
}
