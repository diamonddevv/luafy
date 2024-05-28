package dev.diamond.luafy.script.abstraction.lang;

import com.google.gson.JsonElement;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.BaseValueConversions;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import dev.diamond.luafy.script.abstraction.obj.ScriptObjectProvider;
import dev.diamond.luafy.script.nbt.OptionallyExplicitNbtElement;
import dev.diamond.luafy.util.HexId;
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
    public abstract AdaptableFunction asFunction();
    public abstract HashMap<BaseValue, BaseValue> asMap();
    public abstract Collection<BaseValue> asCollection();
    public abstract Object asJavaObject();

    public int asInt() { return 0; }
    public long asLong() { return 0; }
    public float asFloat() { return 0; }
    public double asDouble() { return 0; }
    public boolean asBoolean() { return false; }

    public <T> T as(Class<T> clazz) { return clazz.cast(value); }
    public <T extends IScriptObject> T asScriptObjectAssertive(Class<T> clazz) {
        return asScriptObjectIfPresent().map(clazz::cast).orElse(null);
    }


    public AbstractBaseValue<?, ?> asBase() { return this; }


    public abstract boolean isString();
    public abstract boolean isInt();
    public abstract boolean isLong();
    public abstract boolean isFloat();
    public abstract boolean isDouble();
    public abstract boolean isBool();
    public abstract boolean isMap();
    public abstract boolean isCollection();
    public abstract boolean isFunction();


    public boolean isNull() { return value == null || value == getLangNull(); }
    public <T> boolean is(Class<T> clazz) {
        return value.getClass() == clazz;
    }


    public LangValue getValue() { return value; }


    public void adaptAndSetOrThrow(Object obj) {
        value = (LangValue) adapt(obj).value;
    }
    public BaseValue adapt(Object obj) {

        if (obj instanceof AbstractBaseValue<?,?> baseValue){
            return (BaseValue) baseValue;
        }

        else if (obj instanceof OptionallyExplicitNbtElement nbt) {
            if (nbt.isExplicit()) {
                assert nbt.type() != null;
                return (BaseValue) BaseValueConversions.explicit_nbtToBase(nbt.nbt(), nbt.type(), this::adapt);
            } else {
                return (BaseValue) BaseValueConversions.implicit_nbtToBase(nbt.nbt(), this::adapt);
            }
        }

        else if (obj instanceof NbtElement nbt) {
            return (BaseValue) BaseValueConversions.implicit_nbtToBase(nbt, this::adapt);
        }

        else if (obj instanceof IScriptObject so) {
            return addObject(() -> so);
        }

        else if (obj instanceof JsonElement jsonElement) {
            return (BaseValue) BaseValueConversions.jsonElementToValue(jsonElement, this::adapt);
        }

        else if (obj instanceof HexId hexid) {
            return adapt(hexid.get());
        }

        return adaptAbstract(obj);
    }


    /**
     * Used in several scenarios to adapt Java Objects to Script values. Some are already completed for you, but here are some important types
     * that need to be manually converted: <br>
     *
     * <ul>
     *     <li>LangValue type (e.g. LuaValue for Lua Scripts): This should be adapted by wrapping with a constructor to BaseValue.</li>
     *     <li>Collection of ?s: All API and ScriptObject functions that return "arrays" actually return collections. Should be adapted by
     *     a BaseValue wrapped around some Language representation of arrays (or lists preferably).</li>
     *     <li>HashMap of ?s to ?s: Same reason as above. Should be adapted by some function returning a BaseValue wrapped around
     *     a Language representation of a Table, Map, Dictionary, or like.</li>
     *     <li>Primitive Types + String: Should be adapted by some catch-all function.</li>
     * </ul>
     *
     * If a type cant be adapted, you should probably throw an exception.
     * I reccommend looking at LuaBaseValue to see how I did it there.
     * <br>
     * <h3>Types that are already adapted:<br></h3>
     * <ul>
     *     <li>AbstractBaseValue (no adaptation needed)</li>
     *     <li>OptionallyExplicitNbtElement</li>
     *     <li>NbtElement</li>
     *     <li>IScriptObject</li>
     *     <li>JsonElement</li>
     *     <li>HexId (adaptation converts to string)</li>
     * </ul>
     *
     * @see dev.diamond.luafy.script.lua.LuaBaseValue
     * @param obj
     * @return value extending AbstractBaseValue.
     */
    public abstract BaseValue adaptAbstract(Object obj);


    /**
     * add a script object (set of functions acting as an object) to the script. <br>
     *
     * Add a HexId to whatever your language representation of this is; use it as a key to cache this object in <code>IScriptObject.CACHE</code>.
     *
     * @see IScriptObject
     * @param obj provider
     */
    public abstract BaseValue addObject(ScriptObjectProvider obj);


    /**
     * Implementation should fetch from IScriptObject.CACHE using key set from addObject.
     *
     * @see AbstractBaseValue#addObject(ScriptObjectProvider)
     * @return Optional containing an IScriptObject, if the type is one. Otherwise, empty.
     */
    public abstract Optional<IScriptObject> asScriptObjectIfPresent();
}
