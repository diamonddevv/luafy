package dev.diamond.luafy.script.api.obj;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import dev.diamond.luafy.script.api.CommandApi;
import dev.diamond.luafy.script.nbt.OptionallyExplicitNbtElement;
import dev.diamond.luafy.util.HexId;
import dev.diamond.luafy.util.LuafyUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class EntityScriptObject implements IScriptObject {

    public final Entity entity;

    public EntityScriptObject(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        set.put("get_name", args -> entity.getName().getString());
        set.put("get_uuid", args -> entity.getUuidAsString());
        set.put("get_nbt", args -> new OptionallyExplicitNbtElement(null, NbtPredicate.entityToNbt(entity)));
        set.put("get_type", args -> Registries.ENTITY_TYPE.getId(entity.getType()).toString());

        set.put("get_position", args -> {
            Collection<Double> doubles = new ArrayList<>();
            doubles.add(entity.getX());
            doubles.add(entity.getY());
            doubles.add(entity.getZ());
            return doubles;
        });

        set.put("get_rotation", args -> {
            Collection<Float> floats = new ArrayList<>();
            floats.add(entity.getPitch());
            floats.add(entity.getYaw());
            return floats;
        });

        set.put("parse_command_as_at", args -> parseAsAt(args[0].asString(), args[1] == null ? 0 : args[1].asInt()));

        set.put("is_living", args -> entity instanceof LivingEntity);
        set.put("is_player", args -> entity instanceof ServerPlayerEntity);

        set.put("test_predicate", args -> LuafyUtil.getAndTestPredicate(args[0].asString(), entity));

        set.put("as_player", args -> new PlayerEntityScriptObject((ServerPlayerEntity) entity));
        set.put("as_living", args -> new LivingEntityScriptObject((LivingEntity) entity));
    }


    private HexId parseAsAt(String command, int raisedPermission) {
        ServerCommandSource source = entity.getCommandSource()
                .withLevel(raisedPermission < 1 ? entity.getCommandSource().level : raisedPermission)
                .withEntity(entity)
                .withPosition(entity.getPos())
                .withSilent()
                .withRotation(entity.getRotationClient());

        var parsed = CommandApi.parseCommand(command, source);

        HexId hexid = HexId.makeNewUnique(ScriptManager.Caches.PREPARSED_COMMANDS.keySet());
        ScriptManager.Caches.PREPARSED_COMMANDS.put(hexid, parsed);
        return hexid;
    }
}
