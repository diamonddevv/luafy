package dev.diamond.luafy.script.abstraction.lang;

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


    public boolean isString() { return false; }
    public boolean isInt() { return false; }
    public boolean isLong() { return false; }
    public boolean isFloat() { return false; }
    public boolean isDouble() { return false; }
    public boolean isBool() { return false; }
    public boolean isFunction() { return false; }
    public boolean isMap() { return false; }
    public boolean isNull() { return value == null || value == getLangNull(); }


    public LangValue getValue() { return value; }
}
