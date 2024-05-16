package dev.diamond.luafy.script.lua;

import dev.diamond.luafy.script.abstraction.lang.AbstractMapValue;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class LuaTableWrapper
        extends AbstractMapValue
        <
                LuaValue,
                LuaTableWrapper,
                LuaTable,
                LuaFunctionWrapper,
                LuaValueWrapper
                        > {


    public LuaTableWrapper(LuaTable value) {
        super(value);
    }

    @Override
    public LuaTableWrapper asMap() {
        return this;
    }

    @Override
    public boolean isMap() {
        return true;
    }

    @Override
    public String asString() {
        return value.checkjstring();
    }

    @Override
    public Object getLangNull() {
        return LuaValue.NIL;
    }
}
