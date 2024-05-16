package dev.diamond.luafy.script.lua;

import dev.diamond.luafy.util.HexId;
import org.luaj.vm2.LuaValue;

public class LuaHexid extends LuaValue {

    private final HexId id;

    public LuaHexid(HexId id) {
        this.id = id;
    }

    public HexId get() {
        return id;
    }

    @Override
    public String tojstring() {
        return id.get();
    }

    @Override
    public int type() {
        return TSTRING;
    }

    @Override
    public String typename() {
        return "hexid";
    }
}
