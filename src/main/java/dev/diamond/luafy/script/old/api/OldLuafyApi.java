package dev.diamond.luafy.script.old.api;

import dev.diamond.luafy.script.lua.LuaHexid;
import dev.diamond.luafy.script.old.Old_LuaScript;
import dev.diamond.luafy.util.HexId;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class OldLuafyApi extends OldAbstractApi {
    private final Old_LuaScript script;

    public OldLuafyApi(Old_LuaScript script) {
        super("luafy");
        this.script = script;
    }

    @Override
    public void create(LuaTable table) {
        // HexIds
        table.set("hexid_as_string", new HexidAsStringFunc());
        table.set("hexid_from_string", new HexidFromStringFunc());
    }

    public static class HexidAsStringFunc extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            return LuaValue.valueOf( ((LuaHexid)arg).get().get() );
        }
    }
    public static class HexidFromStringFunc extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            return new LuaHexid(HexId.fromString(arg.checkjstring()));
        }
    }
}
