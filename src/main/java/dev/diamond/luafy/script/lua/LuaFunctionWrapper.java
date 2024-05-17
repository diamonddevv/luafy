package dev.diamond.luafy.script.lua;

import dev.diamond.luafy.script.abstraction.lang.AbstractFunctionValue;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.util.Optional;

public class LuaFunctionWrapper
        extends AbstractFunctionValue
        <
                LuaValue,
                LuaFunctionWrapper,
                LuaFunction,
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
        var returned = value.invoke(values).arg1();
        return new LuaValueWrapper(returned);
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
    public Optional<LuaFunction> adaptAbstract(Object obj) {
        return Optional.ofNullable((LuaFunction) asBase().adaptAbstract(obj).get());
    }


    @Override
    public String asString() {
        return value.name();
    }

    @Override
    public Object getLangNull() {
        return LuaValue.NIL;
    }


}
