package dev.diamond.luafy.lua.api;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.diamond.luafy.lua.LuaScript;
import dev.diamond.luafy.lua.LuaTypeConversions;
import dev.diamond.luafy.lua.object.EntityLuaObject;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.List;
import java.util.UUID;

public class ServerApi extends AbstractApi {
    private final LuaScript script;

    public ServerApi(LuaScript script) {
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
        table.set("get_uuid_from_player_name", new GetUuidFromUsernameFunc());
        table.set("get_source_uuid", new GetSourceUuidFunc());
        table.set("get_online_player_uuids", new GetOnlinePlayerUuidsFunc());

        // Entity
        table.set("get_source", new GetSourceEntityFunc());
        table.set("get_entity_from_uuid", new GetEntityFromUuidFunc());
        table.set("get_entity", new GetEntityFunc(false));
        table.set("get_entities", new GetEntityFunc(true));
    }

    @Nullable
    private ServerPlayerEntity getPlayer(String uuid) {
        return script.source.getServer().getPlayerManager().getPlayer(UUID.fromString(uuid));
    }

    public class GetPlayerNamesFunc extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            var s = script.source.getServer().getPlayerNames();
            LuaValue[] values = new LuaValue[s.length];
            for (int i = 0; i < s.length; i++) values[i] = LuaTypeConversions.luaFromObj(s[i]);
            return LuaTable.listOf(values);
        }
    }
    public class GetSourceNameFunc extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            var s = script.source.getName();
            return LuaTypeConversions.luaFromObj(s);
        }
    }
    public class GetPlayerNameFromUuidFunc extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            var s = script.source.getServer().getPlayerManager().getPlayer(UUID.fromString(arg.tojstring()));
            if (s == null) {
                return NIL;
            }
            return LuaTypeConversions.luaFromObj(s.getName().getString());
        }
    }

    public class GetSourceUuidFunc extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            var s = script.source;

            if (s.getEntity() != null) {
                String uuid = s.getEntity().getUuidAsString();
                return LuaTypeConversions.luaFromObj(uuid);
            } else return NIL;
        }
    }
    public class GetUuidFromUsernameFunc extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            var s = script.source.getServer().getPlayerManager().getPlayer(arg.tojstring());
            if (s == null) {
                return NIL;
            }
            return LuaTypeConversions.luaFromObj(s.getUuidAsString());
        }
    }


    public class GetOnlinePlayerUuidsFunc extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            var s = script.source.getServer().getPlayerManager().getPlayerList();
            LuaValue[] values = new LuaValue[s.size()];
            for (int i = 0; i < s.size(); i++) values[i] = LuaTypeConversions.luaFromObj(s.get(i).getUuidAsString());
            return LuaTable.listOf(values);
        }
    }


    public class GetSourceEntityFunc extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            if (script.source.getEntity() != null) {
                return new EntityLuaObject(script.source.getEntity());
            } else return NIL;
        }
    }

    public class GetEntityFromUuidFunc extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            return new EntityLuaObject(script.source.getServer().getPlayerManager().getPlayer(UUID.fromString(arg.checkjstring())));
        }
    }

    public class GetEntityFunc extends OneArgFunction {
        private final boolean multiple;

        public GetEntityFunc(boolean allowMultiple) {
            this.multiple = allowMultiple;
        }

        @Override
        public LuaValue call(LuaValue arg) {
            try {
                EntityArgumentType argType = multiple ? EntityArgumentType.entities() : EntityArgumentType.entity();
                EntitySelector selector = argType.parse(new StringReader(arg.checkjstring()));

                if (multiple) {
                    List<? extends Entity> entities = selector.getEntities(script.source);
                    List<EntityLuaObject> luaEntities = entities.stream().map(EntityLuaObject::new).toList();
                    return LuaTypeConversions.arrToLua(luaEntities.toArray());
                } else {
                    Entity entity = selector.getEntity(script.source);
                    return new EntityLuaObject(entity);
                }
            } catch (CommandSyntaxException e) {
                throw new RuntimeException("An exception was caught while parsing an entity selector | Exception: " + e);
            }
        }
    }
}
