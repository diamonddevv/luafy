package dev.diamond.luafy.lua.lib;

import dev.diamond.luafy.lua.LuaScriptManager;
import dev.diamond.luafy.lua.LuaTypeConversions;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.UUID;

public class ServerApi extends AbstractLib {
    private final LuaScriptManager script;

    public ServerApi(LuaScriptManager script) {
        super("server");
        this.script = script;
    }

    @Override
    public void create(LuaTable table) {
        // Names
        table.set("get_player_names", new GetPlayerNamesFunc());
        table.set("get_source_name", new GetSourceNameFunc());
        table.set("get_player_name_from_uuid", new GetPlayerNameFromUuidFunc());

        // Uuids
        table.set("get_source_uuid", new GetSourceUuidFunc());
        table.set("get_online_player_uuids", new GetOnlinePlayerUuidsFunc());
    }

    public class GetPlayerNamesFunc extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            var s = script.source.getServer().getPlayerNames();
            LuaValue[] values = new LuaValue[s.length];
            for (int i = 0; i < s.length; i++) values[i] = LuaTypeConversions.fromJava(s[i]);
            return LuaTable.listOf(values);
        }
    }

    public class GetSourceNameFunc extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            var s = script.source.getName();
            return LuaTypeConversions.fromJava(s);
        }
    }

    public class GetPlayerNameFromUuidFunc extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            var s = script.source.getServer().getPlayerManager().getPlayer(UUID.fromString(arg.tojstring()));
            if (s == null) {
                return NIL;
            }
            return LuaTypeConversions.fromJava(s.getName().getString());
        }
    }

    public class GetSourceUuidFunc extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            var s = script.source;

            if (s.getEntity() != null) {
                String uuid = s.getEntity().getUuidAsString();
                return LuaTypeConversions.fromJava(uuid);
            } else return NIL;
        }
    }

    public class GetOnlinePlayerUuidsFunc extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            var s = script.source.getServer().getPlayerManager().getPlayerList();
            LuaValue[] values = new LuaValue[s.size()];
            for (int i = 0; i < s.size(); i++) values[i] = LuaTypeConversions.fromJava(s.get(i).getUuidAsString());
            return LuaTable.listOf(values);
        }
    }
}
