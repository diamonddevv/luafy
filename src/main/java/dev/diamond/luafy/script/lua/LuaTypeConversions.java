package dev.diamond.luafy.script.lua;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.old.LuafyLua;
import net.minecraft.nbt.*;
import org.apache.commons.lang3.ArrayUtils;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

import static org.luaj.vm2.LuaValue.NIL;

public class LuaTypeConversions {

    private LuaTypeConversions() {}


    public static LuaValue implicitNbtToLua(NbtCompound c, String key) {
        return implicitNbtToLua(c.get(key));
    }
    public static LuaValue explicitNbtToLua(NbtCompound c, String key, String type) {
        return explicitNbtToLua(c.get(key), type);
    }

    public static LuaValue implicitNbtToLua(NbtElement nbt) {
        if (nbt == null) return NIL;
        NbtType<?> type = nbt.getNbtType();

        if (type == NbtDouble.TYPE)
            return LuaValue.valueOf(((NbtDouble)nbt).doubleValue());
        else if (type == NbtString.TYPE)
            return LuaValue.valueOf(nbt.asString());
        else if (type == NbtInt.TYPE)
            return LuaValue.valueOf(((NbtInt)nbt).intValue());
        else if (type == NbtFloat.TYPE)
            return LuaValue.valueOf(((NbtFloat)nbt).floatValue());
        else if (type == NbtByte.TYPE)
            return LuaValue.valueOf(((NbtByte)nbt).byteValue());
        else if (type == NbtShort.TYPE)
            return LuaValue.valueOf(((NbtShort)nbt).shortValue());
        else if (type == NbtLong.TYPE)
            return LuaValue.valueOf(((NbtLong)nbt).longValue());
        else if (type == NbtCompound.TYPE)
            return tableFromNbt((NbtCompound)nbt);
        else if (type == NbtList.TYPE)
            return tableFromNbtList(((NbtList)nbt));
        else if (type == NbtIntArray.TYPE)
            return arrToLua(ArrayUtils.toObject(((NbtIntArray)nbt).getIntArray()));
        else if (type == NbtByteArray.TYPE)
            return arrToLua(ArrayUtils.toObject(((NbtByteArray)nbt).getByteArray()));
        else if (type == NbtLongArray.TYPE)
            return arrToLua(ArrayUtils.toObject(((NbtLongArray)nbt).getLongArray()));

        else {
            Luafy.LOGGER.error("Forbidden Nbt Compound Type (" + type + ")");
        }

        return NIL;
    }
    public static LuaValue explicitNbtToLua(NbtElement nbt, String type) {
        if (Objects.equals(type, LuafyLua.ArgTypes.NUMBER))
            return LuaValue.valueOf(((NbtDouble)nbt).doubleValue());
        else if (Objects.equals(type, LuafyLua.ArgTypes.STRING))
            return LuaValue.valueOf(nbt.asString());
        else if (Objects.equals(type, LuafyLua.ArgTypes.BOOL))
            return LuaValue.valueOf(((NbtByte) nbt).byteValue() == 0b1); // bools are stored in NBT by bytes
        else if (Objects.equals(type, LuafyLua.ArgTypes.TABLE))
            return tableFromNbt((NbtCompound)nbt);
        else if (Objects.equals(type, LuafyLua.ArgTypes.LIST))
            return tableFromNbtList(((NbtList)nbt));
        else {
            Luafy.LOGGER.error("Forbidden Explicit Nbt Compound Type (" + type + ") : Allowed options are [NUM, STR, BOOL, OBJ]");
        }

        return NIL;
    }

    public static void implicitNbtPutObject(NbtCompound nbt, String key, Object obj) {
        if (obj instanceof Integer v) nbt.putInt(key, v);
        else if (obj instanceof Double v) nbt.putDouble(key, v);
        else if (obj instanceof Float v) nbt.putFloat(key, v);
        else if (obj instanceof Short v) nbt.putShort(key, v);
        else if (obj instanceof Long v) nbt.putLong(key, v);
        else if (obj instanceof String v) nbt.putString(key, v);
        else if (obj instanceof Boolean v) nbt.putBoolean(key, v);
        else if (obj instanceof Byte v) nbt.putByte(key, v);
        else if (obj instanceof LuaTable v) putTable(nbt, key, v);
    }
    public static void explicitNbtPutObject(NbtCompound compound, String key, Object obj, String argType) {
        switch (argType) {
            case LuafyLua.ArgTypes.NUMBER -> compound.putDouble(key, (double) obj);
            case LuafyLua.ArgTypes.STRING -> compound.putString(key, (String) obj);
            case LuafyLua.ArgTypes.BOOL -> compound.putBoolean(key, (boolean) obj);
            case LuafyLua.ArgTypes.TABLE -> putTable(compound,key, (LuaTable) obj);
            case LuafyLua.ArgTypes.LIST -> {} // yeah i havent done anything
            default -> {}
        }
    }

    public static void putTable(NbtCompound compound, String key, LuaTable table) {
        compound.put(key, tableToNbt(table));
    }

    public static LuaTable tableFromNbtList(NbtList list) {
        var jvmList = list.stream().toList();
        LuaValue[] arr = new LuaValue[jvmList.size()];
        int i = 0;
        for (NbtElement element : jvmList) {
            arr[i] = implicitNbtToLua(element);
            i++;
        }
        return LuaTable.listOf(arr);
    }

    public static LuaTable tableFromNbt(NbtCompound nbt) {

        LuaTable table = new LuaTable();
        Collection<String> keys = nbt.getKeys();

        for (String k : keys) {
            var value = implicitNbtToLua(nbt, k);
            table.set(k, value);
        }

        return table;
    }
    public static NbtCompound tableToNbt(LuaTable table) {
        NbtCompound nbt = new NbtCompound();

        for (LuaValue k : table.keys()) {
            LuaValue val = table.get(k);
            implicitNbtPutObject(nbt, k.tojstring(), luaToObj(val));
        }

        return nbt;
    }

    public static LuaValue luaFromObj(Object val) {
        return javaToLua(val).arg1();
    }
    public static Object luaToObj(LuaValue lua) {
        return switch (lua.type()) {
            case LuaValue.TNIL -> null;
            case LuaValue.TSTRING -> lua.checkjstring();
            case LuaValue.TNUMBER -> lua.checkdouble();
            case LuaValue.TBOOLEAN -> lua.checkboolean();
            case LuaValue.TTABLE -> lua.checktable();
            default -> throw new IllegalStateException("Unexpected value: " + lua.type());
        };
    }

    public static Object[] arrFromLua(LuaTable tableArray) {
        Object[] arr = new Object[tableArray.narg()];
        for (int i = 0; i < tableArray.narg(); i++) {
            arr[i] = tableArray.get(i + 1); // lua tables are one-indexed
        }
        return arr;
    }

    public static LuaTable arrToLua(Object[] arr) {
        LuaValue[] arr2 = new LuaValue[arr.length];
        int i = 0;
        for (var o : arr) {
            arr2[i] = luaFromObj(arr[i]);
            i++;
        }
        return LuaTable.listOf(arr2);
    }

    public static LuaValue jsonElementToLuaValue(JsonElement e) {
        LuaValue value = null;
        if (e.isJsonPrimitive()) {
            JsonPrimitive p = e.getAsJsonPrimitive();

            if (p.isNumber()) {
                value = LuaValue.valueOf(p.getAsDouble());
            } else if (p.isString()) {
                value = LuaValue.valueOf(p.getAsString());
            } else if (p.isBoolean()) {
                value = LuaValue.valueOf(p.getAsBoolean());
            }

        } else if (e.isJsonArray()) {

            JsonArray array = e.getAsJsonArray();
            LuaValue[] arr = new LuaValue[array.size()];

            int i = 0;
            for (var e2 : array) {
                arr[i] = jsonElementToLuaValue(e2);
                i++;
            }

            value = LuaTable.listOf(arr);

        } else if (e.isJsonObject()) {
            value = jsonObjToLuaTable(e.getAsJsonObject());
        } else if (e.isJsonNull()) {
            value = NIL;
        }

        return value;
    }
    public static LuaTable jsonObjToLuaTable(JsonObject obj) {
        LuaTable table = LuaTable.tableOf();
        for (var key : obj.keySet()) {
            JsonElement e = obj.get(key);
            LuaValue value = jsonElementToLuaValue(e);
            if (value != null) table.set(LuaValue.valueOf(key), value);
        }
        return table;
    }


    public static LuaTable hashToLua(HashMap<?,?> hash, Function<Object, LuaValue> adapter) {
        LuaTable table = new LuaTable();
        for (var kvp : hash.entrySet()) {

            var key = adapter.apply(kvp.getKey());
            var value = adapter.apply(kvp.getValue());

            table.set(key, value);
        }
        return table;
    }

    // all below is yoinked from figura - some is slightly edited though. might eventually be replaced with my own stuff
    public static Varargs javaToLua(Object val) {
        if (val == null)
            return NIL;
        else if (val instanceof LuaValue l)
            return l;
        else if (val instanceof Double d)
            return LuaValue.valueOf(d);
        else if (val instanceof String s)
            return LuaValue.valueOf(s);
        else if (val instanceof Boolean b)
            return LuaValue.valueOf(b);
        else if (val instanceof Integer i)
            return LuaValue.valueOf(i);
        else if (val instanceof Float f)
            return LuaValue.valueOf(f);
        else if (val instanceof Byte b)
            return LuaValue.valueOf(b);
        else if (val instanceof Long l)
            return LuaValue.valueOf(l);
        else if (val instanceof Character c)
            return LuaValue.valueOf(c);
        else if (val instanceof Short s)
            return LuaValue.valueOf(s);
        else if (val instanceof Collection<?> collection)
            return wrapArray(collection.toArray());
        else if (val.getClass().isArray())
            return wrapArray(val);
        else {
            throw new IllegalStateException("Forbidden Lua Type (" + val.getClass() + ")");
        }
    }
    public static Varargs wrapArray(Object array) {
        int len = Array.getLength(array);
        LuaValue[] args = new LuaValue[len];

        for (int i = 0; i < len; i++)
            args[i] = javaToLua(Array.get(array, i)).arg1();

        return LuaValue.varargsOf(args);
    }

}
