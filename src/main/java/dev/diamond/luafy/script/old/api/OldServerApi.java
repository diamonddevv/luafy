package dev.diamond.luafy.script.old.api;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.diamond.luafy.script.lua.LuaHexid;
import dev.diamond.luafy.script.old.Old_LuaScript;
import dev.diamond.luafy.script.lua.LuaTypeConversions;
import dev.diamond.luafy.script.old.LuafyLua;
import dev.diamond.luafy.script.old.object.EntityLuaObject;
import dev.diamond.luafy.util.HexId;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OldServerApi extends OldAbstractApi {
    private final Old_LuaScript script;

    public OldServerApi(Old_LuaScript script) {
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

        // Groups
        table.set("group_entities", new GroupEntitiesFunc());
        table.set("get_entity_group", new GetGroupedEntitiesFunc());
    }

    @Nullable
    private ServerPlayerEntity getPlayer(String uuid) {
        return script.source.getServer().getPlayerManager().getPlayer(UUID.fromString(uuid));
    }

    private List<? extends Entity> selectEntities(boolean multiple, String selector) {
        EntityArgumentType argType = multiple ? EntityArgumentType.entities() : EntityArgumentType.entity();
        try {
            EntitySelector s = argType.parse(new StringReader(selector));
            if (multiple) {
                return s.getEntities(script.source);
            } else {
                List<Entity> es = new ArrayList<>();
                es.add(s.getEntity(script.source));
                return es;
            }
        } catch (CommandSyntaxException e) {
            throw new RuntimeException("An exception was caught while parsing an entity selector | Exception: " + e);
        }
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
            List<? extends Entity> entities = selectEntities(multiple, arg.checkjstring());
            if (multiple) {
                List<EntityLuaObject> luaEntities = entities.stream().map(EntityLuaObject::new).toList();
                return LuaTypeConversions.arrToLua(luaEntities.toArray());
            } else {
                Entity entity = entities.get(0);
                return new EntityLuaObject(entity);
            }
        }
    }


    public class GroupEntitiesFunc extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            HexId hexid = HexId.makeNewUnique(LuafyLua.ScriptManagements.ENTITY_GROUP_CACHE.keySet());
            var entities = selectEntities(true, arg.checkjstring());
            LuafyLua.ScriptManagements.ENTITY_GROUP_CACHE.put(hexid, entities);
            return new LuaHexid(hexid);
        }
    }
    public class GetGroupedEntitiesFunc extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            if (!(arg instanceof LuaHexid)) return NIL;
            var entities = ((LuaHexid) arg).get().getHashed(LuafyLua.ScriptManagements.ENTITY_GROUP_CACHE);
            if (entities == null) return NIL;
            EntityLuaObject[] luaEs = new EntityLuaObject[entities.size()];

            int i = 0;
            for (var e : entities) { luaEs[i] = new EntityLuaObject(e); i++; }

            return LuaTypeConversions.arrToLua(luaEs);
        }
    }
}
