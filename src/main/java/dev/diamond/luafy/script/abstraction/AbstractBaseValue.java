package dev.diamond.luafy.script.abstraction;

public abstract class AbstractBaseValue
        <
                V,
                F extends AbstractFunctionValue<?, ?, ?, ?, ?>,
                M extends AbstractMapValue<?, ?, ?, ?, ?>
                > {

    public V value;

    public AbstractBaseValue(V value) {
        this.value = value;
    }

    public abstract String asString();
    public int asInt() { return 0; }
    public long asLong() { return 0; }
    public float asFloat() { return 0; }
    public double asDouble() { return 0; }
    public boolean asBoolean() { return false; }
    public F asFunction() { return null; }
    public M asMap() { return null; }


    public boolean isString() { return false; }
    public boolean isInt() { return false; }
    public boolean isLong() { return false; }
    public boolean isFloat() { return false; }
    public boolean isDouble() { return false; }
    public boolean isBool() { return false; }
    public boolean isFunction() { return false; }
    public boolean isMap() { return false; }


    public V getValue() { return value; }
}
