package dev.diamond.luafy.script.abstraction;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import net.minecraft.nbt.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import static dev.diamond.luafy.script.ScriptManager.ExplicitType.*;

public class BaseValueConversions {

    public static AbstractBaseValue<?, ?, ?> implicit_nbtToBase(NbtElement nbt, LangTypeAdapter adapter) {
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
    public static AbstractBaseValue<?, ?, ?> explicit_nbtToBase(NbtElement nbt, ScriptManager.ExplicitType type, LangTypeAdapter adapter) {
        return switch (type) {
            case DOUBLE -> adapter.adapt(((NbtDouble) nbt).doubleValue());
            case STRING -> adapter.adapt(nbt.asString());
            case BOOLEAN -> adapter.adapt(((NbtByte) nbt).byteValue());
            case OBJECT -> adapter.adapt(nbtListToBase(((NbtList) nbt), adapter));
            case LIST -> adapter.adapt(nbtObjToBase(((NbtCompound) nbt), adapter));
        };
    }


    public static Collection<AbstractBaseValue<?, ?, ?>> nbtListToBase(NbtList list, LangTypeAdapter adapter) {
        Collection<AbstractBaseValue<?, ?, ?>> collection = new ArrayList<>();
        for (NbtElement element : list) {
            collection.add(implicit_nbtToBase(element, adapter));
        }
        return collection;
    }
    public static HashMap<AbstractBaseValue<?, ?, ?>, AbstractBaseValue<?, ?, ?>> nbtObjToBase(NbtCompound nbt, LangTypeAdapter adapter) {
        HashMap<AbstractBaseValue<?, ?, ?>, AbstractBaseValue<?, ?, ?>> hash = new HashMap<>();
        for (String key : nbt.getKeys()) {
            var element = nbt.get(key);
            hash.put(adapter.adapt(key), adapter.adapt(element));
        }
        return hash;
    }

    public static AbstractBaseValue<?, ?, ?> implicit_nbtToBaseWithKey(NbtCompound compound, String key, LangTypeAdapter adapter) {
        return implicit_nbtToBase(Objects.requireNonNull(compound.get(key)), adapter);
    }

    public static AbstractBaseValue<?, ?, ?> explicit_nbtToBaseWithKey(NbtCompound compound, String key, ScriptManager.ExplicitType type, LangTypeAdapter adapter) {
        return explicit_nbtToBase(Objects.requireNonNull(compound.get(key)), type, adapter);
    }

}
