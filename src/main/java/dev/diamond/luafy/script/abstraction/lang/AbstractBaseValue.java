package dev.diamond.luafy.script.abstraction.lang;

import dev.diamond.luafy.script.abstraction.BaseValueConversions;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import dev.diamond.luafy.script.abstraction.obj.ScriptObjectProvider;
import dev.diamond.luafy.script.nbt.OptionallyExplicitNbtElement;
import net.minecraft.nbt.NbtElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public abstract class AbstractBaseValue
        <
                LangValue,
                BaseValue extends AbstractBaseValue<?, ?>
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
    public HashMap<BaseValue, BaseValue> asMap() { return null; }
    public Collection<BaseValue> asCollection() { return null; }

    public <T> T as(Class<T> clazz) { return clazz.cast(value); }

    public AbstractBaseValue<?, ?> asBase() { return this; }

    public boolean isString() { return false; }
    public boolean isInt() { return false; }
    public boolean isLong() { return false; }
    public boolean isFloat() { return false; }
    public boolean isDouble() { return false; }
    public boolean isBool() { return false; }
    public boolean isMap() { return false; }
    public boolean isCollection() { return false; }
    public boolean isNull() { return value == null || value == getLangNull(); }

    public <T> boolean is(Class<T> clazz) {
        return value.getClass() == clazz;
    }


    public LangValue getValue() { return value; }


    public void adaptAndSetOrThrow(Object obj) {
        value = adapt(obj);
    }
    public LangValue adapt(Object obj) {

        if (obj instanceof OptionallyExplicitNbtElement nbt) {
            if (nbt.isExplicit()) {
                assert nbt.type() != null;
                obj = BaseValueConversions.explicit_nbtToBase(nbt.nbt(), nbt.type(), this::adaptAbstract);
            } else {
                obj = BaseValueConversions.implicit_nbtToBase(nbt.nbt(), this::adaptAbstract);
            }
        }

        if (obj instanceof IScriptObject so) {
            obj = addObject(() -> so);
        }

        return (LangValue) adaptAbstract(obj).value;
    }
    public abstract BaseValue adaptAbstract(Object obj);


    /**
     * add a script object (set of functions acting as an object) to the script.
     *
     * @param obj provider
     */
    public abstract BaseValue addObject(ScriptObjectProvider obj);
}
