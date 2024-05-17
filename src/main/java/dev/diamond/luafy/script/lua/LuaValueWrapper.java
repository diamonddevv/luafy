package dev.diamond.luafy.script.lua;

import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.util.HexId;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Collection;
import java.util.HashMap;

public class LuaValueWrapper extends AbstractBaseValue<LuaValue, LuaFunctionWrapper, LuaValueWrapper> {

    public LuaValueWrapper(LuaValue value) {
        super(value);
    }

    @Override public String asString() {
        return value.checkjstring();
    }
    @Override public Object getLangNull() {
        return LuaValue.NIL;
    }
    @Override public int asInt() {
        return value.checkint();
    }
    @Override public long asLong() {
        return value.checklong();
    }
    @Override public float asFloat() {
        return (float) value.checkdouble();
    }
    @Override public double asDouble() {
        return value.checkdouble();
    }
    @Override public boolean asBoolean() {
        return value.checkboolean();
    }
    @Override public LuaFunctionWrapper asFunction() {
        return new LuaFunctionWrapper(value.checkfunction());
    }
    @Override public HashMap<LuaValueWrapper, LuaValueWrapper> asMap() {
        HashMap<LuaValueWrapper, LuaValueWrapper> hash = new HashMap<>();
        LuaTable table = value.checktable();
        for (int i = 0; i < table.narg(); i++) {
            hash.put(new LuaValueWrapper(table.keys()[i]), new LuaValueWrapper(table.get(table.keys()[i])));
        }

        return hash;
    }

    @Override public boolean isString() {
        return value.isstring();
    }
    @Override public boolean isInt() {
        return value.isint();
    }
    @Override public boolean isLong() {
        return value.islong();
    }
    @Override public boolean isFloat() {
        return value.isnumber();
    }
    @Override public boolean isDouble() {
        return value.isnumber();
    }
    @Override public boolean isBool() {
        return value.isboolean();
    }
    @Override public boolean isFunction() {
        return value.isfunction();
    }
    @Override public boolean isMap() {
        return value.istable();
    }

    @Override
    public LuaValue adaptAbstract(Object obj) {
        try {
            if (obj instanceof LuaValue luaval)
                return luaval;
            else if (obj instanceof LuaValueWrapper wrapper)
                return wrapper.getValue();
            else if (obj instanceof HashMap<?, ?> hash)
                return LuaTypeConversions.hashToLua(hash, this::adapt);
            else if (obj instanceof Collection<?> collection)
                return LuaTypeConversions.arrToLua(collection.toArray());
            else if (obj instanceof HexId hexid)
                return new LuaHexid(hexid);
            else
                return LuaTypeConversions.luaFromObj(obj);
        } catch (Exception e) {
            throw new RuntimeException("Could not adapt type " + obj.getClass() + ": " + e);
        }
    }

}
