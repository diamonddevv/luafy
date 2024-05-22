package dev.diamond.luafy.script.api;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.api.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.api.obj.EntityScriptObject;
import dev.diamond.luafy.script.api.obj.PlayerEntityScriptObject;
import dev.diamond.luafy.util.HexId;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ServerApi extends AbstractScriptApi {
    public ServerApi(AbstractScript<?> script) {
        super(script, "server");
    }

    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        HashMap<String, AdaptableFunction> f = new HashMap<>();

        f.put("get_player_names", args -> Arrays.stream(script.source.getServer().getPlayerNames()).toList());
        f.put("get_source_name", args -> script.source.getName());
        f.put("get_player_name_from_uuid", args -> {
            var player = getPlayer(args[0].asString());
            return player != null ? player.getName() : null;
        });

        f.put("get_uuid_from_player_name", args -> {
            var player = script.source.getServer().getPlayerManager().getPlayer(args[0].asString());
            return player != null ? player.getUuidAsString() : null;
        });
        f.put("get_source_uuid", args ->{
            if (script.source.getEntity() != null) {
                return script.source.getEntity().getUuidAsString();
            } else return null;
        });
        f.put("get_online_player_uuids", args -> {
            var players = script.source.getServer().getPlayerManager().getPlayerList();
            return players.stream().map(Entity::getUuidAsString).toList();
        });

        f.put("get_source_entity", args -> new EntityScriptObject(script.source.getEntity()));
        f.put("get_entity_from_uuid", args -> {
            UUID uuid = UUID.fromString(args[0].asString());
            ServerWorld dimension = script.source.getWorld();
            var e = dimension.getEntity(uuid);
            return new EntityScriptObject(e);
        });
        f.put("get_player_from_uuid", args -> {
            UUID uuid = UUID.fromString(args[0].asString());
            var e = script.source.getServer().getPlayerManager().getPlayer(uuid);
            return new PlayerEntityScriptObject(e);
        });
        f.put("get_entity", args -> selectEntities(false, args[0].asString()).stream().map(EntityScriptObject::new).toList());
        f.put("get_entities", args -> selectEntities(true, args[0].asString()).stream().map(EntityScriptObject::new).toList());

        f.put("group_entities", args -> {
            var entities = selectEntities(true, args[0].asString()).stream().map(EntityScriptObject::new).toList();
            var hexid = HexId.makeNewUnique(ScriptManager.Caches.GROUPED_ENTITIES.keySet());
            ScriptManager.Caches.GROUPED_ENTITIES.put(hexid, entities);
            return hexid;
        });
        f.put("get_entity_group", args -> HexId.fromString(args[0].asString()).getHashed(ScriptManager.Caches.GROUPED_ENTITIES));
        f.put("remove_entity_group", args -> {
            ScriptManager.Caches.GROUPED_ENTITIES.remove(HexId.fromString(args[0].asString()));
            return null;
        });

        f.put("get_world_time", args -> script.source.getWorld().getTime());
        f.put("get_days", args -> script.source.getWorld().getTimeOfDay() / 24000L);

        return f;
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
}