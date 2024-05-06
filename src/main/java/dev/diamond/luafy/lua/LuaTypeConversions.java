package dev.diamond.luafy.lua;

import dev.diamond.luafy.Luafy;
import net.minecraft.nbt.*;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Objects;

public class LuaTypeConversions {

    private LuaTypeConversions() {}


    public static LuaValue implicitNbtToLua(NbtCompound c, String key) {
        NbtElement nbt = c.get(key);
        if (nbt == null) return LuaValue.NIL;
        NbtType<?> type = nbt.getNbtType();

        if (type == NbtDouble.TYPE)
            return LuaValue.valueOf(c.getDouble(key));
        else if (type == NbtString.TYPE)
            return LuaValue.valueOf(c.getString(key));
        else if (type == NbtInt.TYPE)
            return LuaValue.valueOf(c.getInt(key));
        else if (type == NbtFloat.TYPE)
            return LuaValue.valueOf(c.getFloat(key));
        else if (type == NbtByte.TYPE)
            return LuaValue.valueOf(c.getByte(key));
        else if (type == NbtShort.TYPE)
            return LuaValue.valueOf(c.getShort(key));
        else if (type == NbtCompound.TYPE)
            return tableFromNbt(c, key);
        else {
            Luafy.LOGGER.error("Forbidden Nbt Compound Type (" + type + ")");
        }

        return LuaValue.NIL;
    }

    public static LuaValue explicitNbtToLua(NbtCompound c, String key, String type) {
        if (Objects.equals(type, LuafyLua.ArgTypes.NUMBER))
            return LuaValue.valueOf(c.getDouble(key));
        else if (Objects.equals(type, LuafyLua.ArgTypes.STRING))
            return LuaValue.valueOf(c.getString(key));
        else if (Objects.equals(type, LuafyLua.ArgTypes.BOOL))
            return LuaValue.valueOf(c.getBoolean(key));
        else if (Objects.equals(type, LuafyLua.ArgTypes.TABLE))
            return tableFromNbt(c, key);
        else {
            Luafy.LOGGER.error("Forbidden Explicit Nbt Compound Type (" + type + ") : Allowed options are [NUM, STR, BOOL, OBJ]");
        }

        return LuaValue.NIL;
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

    public static void nbtPutObject(NbtCompound nbt, String key, Object obj) {
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

    public static void nbtPutObject(NbtCompound compound, String key, Object obj, String argType) {
        switch (argType) {
            case LuafyLua.ArgTypes.NUMBER -> compound.putDouble(key, (double) obj);
            case LuafyLua.ArgTypes.STRING -> compound.putString(key, (String) obj);
            case LuafyLua.ArgTypes.BOOL -> compound.putBoolean(key, (boolean) obj);
            case LuafyLua.ArgTypes.TABLE -> putTable(compound,key, (LuaTable) obj);
            default -> {}
        }
    }

    public static LuaTable tableFromNbt(NbtCompound compound, String key) {
        NbtCompound nbt = compound.getCompound(key);
        LuaTable table = new LuaTable();
        Collection<String> keys = nbt.getKeys();

        for (String k : keys) {
            var value = implicitNbtToLua(nbt, k);
            table.set(k, value);
        }

        return table;
    }

    public static void putTable(NbtCompound compound, String key, LuaTable table) {
        NbtCompound nbt = new NbtCompound();

        for (LuaValue k : table.keys()) {
            LuaValue val = table.get(k);
            nbtPutObject(nbt, k.tojstring(), luaToObj(val));
        }

        compound.put(key, nbt);
    }

    public static LuaValue fromJava(Object val) {
        return javaToLua(val).arg1();
    }

    // all below is yoinked from figura !! some is edited though

    public static Varargs javaToLua(Object val) {
        if (val == null)
            return LuaValue.NIL;
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
            Luafy.LOGGER.error("Forbidden Lua Type (" + val.getClass() + ")");
            return LuaValue.NIL;
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
