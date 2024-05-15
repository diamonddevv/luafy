package dev.diamond.luafy.script.old.api;

import dev.diamond.luafy.script.old.Old_LuaScript;
import dev.diamond.luafy.util.HexId;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class LuafyApi extends AbstractApi {
    private final Old_LuaScript script;

    public LuafyApi(Old_LuaScript script) {
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
            return LuaValue.valueOf( ((HexId)arg).get() );
        }
    }
    public static class HexidFromStringFunc extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            return HexId.fromString(arg.checkjstring());
        }
    }
}
