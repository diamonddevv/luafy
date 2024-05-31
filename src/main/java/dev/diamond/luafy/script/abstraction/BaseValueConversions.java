package dev.diamond.luafy.script.abstraction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import net.minecraft.nbt.*;

import java.util.*;

import static dev.diamond.luafy.script.ScriptManager.ExplicitType.*;

public class BaseValueConversions {

    public static AbstractBaseValue<?, ?> implicit_nbtToBase(NbtElement nbt, BaseValueAdapter adapter) {
        ScriptManager.ExplicitType t;

        NbtType<?> type = nbt.getNbtType();

        if      (type == NbtString.TYPE)    t = STRING;
        else if (type == NbtDouble.TYPE)    t = DOUBLE;
        else if (type == NbtInt.TYPE)       t = DOUBLE;
        else if (type == NbtFloat.TYPE)     t = DOUBLE;
        else if (type == NbtByte.TYPE)      t = DOUBLE;
        else if (type == NbtShort.TYPE)     t = DOUBLE;
        else if (type == NbtLong.TYPE)      t = DOUBLE;
        else if (type == NbtCompound.TYPE)  t = OBJECT;
        else if (type == NbtList.TYPE)      t = LIST;
        else if (type == NbtIntArray.TYPE)  t = LIST;
        else if (type == NbtByteArray.TYPE) t = LIST;
        else if (type == NbtLongArray.TYPE) t = LIST;

        else {
            throw new RuntimeException("Forbidden Nbt Compound Type (" + type + ")");
        }

        return explicit_nbtToBase(nbt, t, adapter);
    }
    public static AbstractBaseValue<?, ?> explicit_nbtToBase(NbtElement nbt, ScriptManager.ExplicitType type, BaseValueAdapter adapter) {
        return switch (type) {
            case DOUBLE -> adapter.adapt(((AbstractNbtNumber) nbt).doubleValue());
            case STRING -> adapter.adapt(nbt.asString());
            case BOOLEAN -> adapter.adapt(((NbtByte) nbt).byteValue());
            case LIST -> adapter.adapt(nbtListToBase(((AbstractNbtList<?>) nbt), adapter));
            case OBJECT -> adapter.adapt(nbtObjToBase(((NbtCompound) nbt), adapter));
        };
    }

    public static Collection<AbstractBaseValue<?, ?>> nbtListToBase(AbstractNbtList<?> list, BaseValueAdapter adapter) {
        Collection<AbstractBaseValue<?, ?>> collection = new ArrayList<>();
        for (NbtElement element : list) {
            collection.add(implicit_nbtToBase(element, adapter));
        }
        return collection;
    }
    public static HashMap<AbstractBaseValue<?, ?>, AbstractBaseValue<?, ?>> nbtObjToBase(NbtCompound nbt, BaseValueAdapter adapter) {
        HashMap<AbstractBaseValue<?, ?>, AbstractBaseValue<?, ?>> hash = new HashMap<>();
        for (String key : nbt.getKeys()) {
            var element = nbt.get(key);
            if (element != null) hash.put(adapter.adapt(key), adapter.adapt(implicit_nbtToBase(element, adapter)));
        }
        return hash;
    }

    public static AbstractBaseValue<?, ?> implicit_nbtToBaseWithKey(NbtCompound compound, String key, BaseValueAdapter adapter) {
        return implicit_nbtToBase(Objects.requireNonNull(compound.get(key)), adapter);
    }
    public static AbstractBaseValue<?, ?> explicit_nbtToBaseWithKey(NbtCompound compound, String key, ScriptManager.ExplicitType type, BaseValueAdapter adapter) {
        return explicit_nbtToBase(Objects.requireNonNull(compound.get(key)), type, adapter);
    }

    public static NbtElement implicit_baseToNbt(AbstractBaseValue<?, ?> value) {
        ScriptManager.ExplicitType t;


        if      (value.isDouble())      t = DOUBLE;
        else if (value.isFloat())       t = DOUBLE;
        else if (value.isInt())         t = DOUBLE;
        else if (value.isLong())        t = DOUBLE;
        else if (value.isBool())        t = BOOLEAN;
        else if (value.isMap())         t = OBJECT;
        else if (value.isCollection())  t = LIST;
        else if (value.isString())      t = STRING;
        else throw new RuntimeException("Couldn't convert base value of " + value.value + " to NBT.");

        return explicit_baseToNbt(t, value);
    }
    public static NbtElement explicit_baseToNbt(ScriptManager.ExplicitType type, AbstractBaseValue<?, ?> value) {
        return switch (type) {
            case DOUBLE ->  NbtDouble.of(value.asDouble());
            case STRING -> NbtString.of(value.asString());
            case BOOLEAN -> NbtByte.of(value.asBoolean());
            case OBJECT -> mapToCompound((HashMap<AbstractBaseValue<?, ?>, AbstractBaseValue<?, ?>>) value.asMap());
            case LIST -> collectionToList((Collection<AbstractBaseValue<?, ?>>)value.asCollection());
        };
    }

    public static NbtList collectionToList(Collection<AbstractBaseValue<?,?>> collection) {
        NbtList nbt = new NbtList();
        for (var k : collection) {
            nbt.add(implicit_baseToNbt(k));
        }
        return nbt;
    }
    public static NbtCompound mapToCompound(HashMap<AbstractBaseValue<?, ?>, AbstractBaseValue<?, ?>> map) {
        NbtCompound nbt = new NbtCompound();
        for (var kvp : map.entrySet()) {
            nbt.put(kvp.getKey().asString(), implicit_baseToNbt(kvp.getValue()));
        }
        return nbt;
    }

    public static void implicit_putBaseToNbt(NbtCompound nbt, String address, AbstractBaseValue<?, ?> base) {
        NbtElement element = implicit_baseToNbt(base);
        nbt.put(address, element);
    }
    public static void explicit_putBaseToNbt(NbtCompound nbt, String address, ScriptManager.ExplicitType type, AbstractBaseValue<?, ?> base) {
        NbtElement element = explicit_baseToNbt(type, base);
        nbt.put(address, element);
    }

    public static AbstractBaseValue<?, ?> jsonElementToValue(JsonElement e,  BaseValueAdapter adapter) {
        AbstractBaseValue<?, ?> value = null;
        if (e.isJsonPrimitive()) {
            JsonPrimitive p = e.getAsJsonPrimitive();

            if (p.isNumber()) {
                value = adapter.adapt(p.getAsDouble());
            } else if (p.isString()) {
                value = adapter.adapt(p.getAsString());
            } else if (p.isBoolean()) {
                value = adapter.adapt(p.getAsBoolean());
            }

        } else if (e.isJsonArray()) {

            JsonArray array = e.getAsJsonArray();
            AbstractBaseValue<?, ?>[] arr = new AbstractBaseValue<?, ?>[array.size()];

            int i = 0;
            for (var e2 : array) {
                arr[i] = jsonElementToValue(e2, adapter);
                i++;
            }

            value = adapter.adapt(Arrays.stream(arr).toList());

        } else if (e.isJsonObject()) {
            value = jsonObjToValue(e.getAsJsonObject(), adapter);
        } else if (e.isJsonNull()) {
            value = adapter.adapt(null);
        }

        return value;
    }
    public static AbstractBaseValue<?, ?> jsonObjToValue(JsonObject obj, BaseValueAdapter adapter) {
        HashMap<AbstractBaseValue<?, ?>, AbstractBaseValue<?, ?>> map = new HashMap<>();
        for (var key : obj.keySet()) {
            JsonElement e = obj.get(key);
            AbstractBaseValue<?, ?> value = jsonElementToValue(e, adapter);
            if (value != null) map.put(adapter.adapt(key), value);
        }
        return adapter.adapt(map);
    }

}
