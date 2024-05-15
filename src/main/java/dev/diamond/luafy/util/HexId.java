package dev.diamond.luafy.util;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class HexId extends LuaValue {
    public final String stringId;
    public final long longId;

    private HexId(String stringId, long longId) {
        this.stringId = stringId;
        this.longId = longId;
    }

    public String get() {
        return stringId;
    }

    public boolean matches(HexId other) {
        return longId == other.longId;
    }


    @Override
    public int type() {
        return TSTRING;
    }

    @Override
    public String typename() {
        return "hexid";
    }

    @Override
    public String tojstring() {
        return get();
    }

    @Override
    public String toString() {
        return stringId;
    }

    public <T> T getHashed(HashMap<HexId, T> hash) {
        return getHashed(hash, this);
    }

    public static <T> T getHashed(HashMap<HexId, T> hash, HexId key) {
        for (var v : hash.entrySet()) {
            if (v.getKey().matches(key)) {
                return v.getValue();
            }
        }
        return null;
    }


    public static HexId makeNewUnique(Collection<HexId> others) {
        HexId id = null;
        while (id == null || others.stream().anyMatch(id::matches)) {
            id = makeNew(8);
        }
        return id;
    }

    public static HexId fromString(String string) {
        long id = Long.decode("0x" + string);
        if (!Long.toHexString(id).equals(string)) throw new RuntimeException("Could not deserialize HexId");
        return new HexId(string, id);
    }

    private static HexId makeNew(int digits) {
        long lim = LuafyMath.longpow(16, digits);
        Random random = new Random();
        long l = random.nextLong(0, lim);
        return new HexId(Long.toHexString(l), l);
    }

}
