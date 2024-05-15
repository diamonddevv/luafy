package dev.diamond.luafy.script.lua;

import dev.diamond.luafy.script.abstraction.AbstractFunctionValue;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

public class LuaFunctionWrapper
        extends AbstractFunctionValue
        <
                LuaValue,
                LuaFunctionWrapper,
                LuaFunction,
                LuaTableWrapper,
                LuaValueWrapper
                > {

    public LuaFunctionWrapper(LuaFunction value) {
        super(value);
    }

    @Override
    public LuaValueWrapper invoke(LuaValueWrapper[] params) {
        LuaValue[] values = new LuaValue[params.length];

        for (int i = 0; i < params.length; i++) {
            values[i] = params[i].getValue();
        }

        LuaValue result = value.invoke(values).arg1();

        return new LuaValueWrapper(result);
    }

    @Override
    public LuaFunctionWrapper asFunction() {
        return this;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public String asString() {
        return value.name();
    }


}
