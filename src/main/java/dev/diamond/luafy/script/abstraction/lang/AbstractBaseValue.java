package dev.diamond.luafy.script.abstraction.lang;

import net.minecraft.data.DataGenerator;

public abstract class AbstractBaseValue
        <
                LangValue,
                FuncValue extends AbstractFunctionValue<?, ?, ?, ?, ?>,
                MapValue extends AbstractMapValue<?, ?, ?, ?, ?>
                > {

    public LangValue value;

    public AbstractBaseValue(LangValue value) {
        this.value = value;
    }

    public abstract String asString();
    public abstract Object getLangNull();
    public int asInt() { return 0; }
    public long asLong() { return 0; }
    public float asFloat() { return 0; }
    public double asDouble() { return 0; }
    public boolean asBoolean() { return false; }
    public FuncValue asFunction() { return null; }
    public MapValue asMap() { return null; }

    public <T> T as(Class<T> clazz) { return clazz.cast(value); }

    public AbstractBaseValue<?, ?, ?> asBase() { return this; }


    public boolean isString() { return false; }
    public boolean isInt() { return false; }
    public boolean isLong() { return false; }
    public boolean isFloat() { return false; }
    public boolean isDouble() { return false; }
    public boolean isBool() { return false; }
    public boolean isFunction() { return false; }
    public boolean isMap() { return false; }
    public boolean isNull() { return value == null || value == getLangNull(); }

    public <T> boolean is(Class<T> clazz) {
        return value.getClass() == clazz;
    }


    public LangValue getValue() { return value; }


    public void adaptAndSetOrThrow(Object obj) {
        value = adapt(obj);
    }
    public abstract LangValue adapt(Object obj);
}
