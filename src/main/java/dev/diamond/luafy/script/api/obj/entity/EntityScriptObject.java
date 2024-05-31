package dev.diamond.luafy.script.api.obj.entity;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import dev.diamond.luafy.script.api.CommandApi;
import dev.diamond.luafy.script.api.obj.math.Vec3dScriptObject;
import dev.diamond.luafy.script.nbt.OptionallyExplicitNbtElement;
import dev.diamond.luafy.util.HexId;
import dev.diamond.luafy.util.LuafyUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

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

        set.put("get_motion", args -> new Vec3dScriptObject(entity.getVelocity()));
        set.put("get_position", args -> new Vec3dScriptObject(entity.getPos()));

        set.put("get_pitch", args -> entity.getPitch());
        set.put("get_yaw", args -> entity.getYaw());

        set.put("parse_command_as_at", args -> parseAsAt(args[0].asString(), args[1] == null ? 0 : args[1].asInt()));

        set.put("is_living", args -> entity instanceof LivingEntity);
        set.put("is_player", args -> entity instanceof ServerPlayerEntity);

        set.put("test_predicate", args -> LuafyUtil.getAndTestPredicate(args[0].asString(), entity));

        set.put("as_player", args -> new PlayerEntityScriptObject((ServerPlayerEntity) entity));
        set.put("as_living", args -> new LivingEntityScriptObject((LivingEntity) entity));


        // motion
        set.put("set_motion", args -> {
            Vec3d vec3d = args[0].asScriptObjectAssertive(Vec3dScriptObject.class).get();
            entity.setVelocity(vec3d);
            entity.velocityModified = true;
            return null;
        });
        set.put("add_motion", args -> {
            Vec3d vec3d = args[0].asScriptObjectAssertive(Vec3dScriptObject.class).get();
            entity.addVelocity(vec3d);
            entity.velocityModified = true;
            return null;
        });
        set.put("multiply_motion", args -> {
            Vec3d vec3d = args[0].asScriptObjectAssertive(Vec3dScriptObject.class).get();
            entity.setVelocity(entity.getVelocity().multiply(vec3d));
            entity.velocityModified = true;
            return null;
        });

        // vector
        set.put("angle_towards", args -> {
            Vec3d pos = entity.getPos();
            Vec3d target = args[0].asScriptObjectAssertive(Vec3dScriptObject.class).get();


            double dz = target.z - pos.z;
            double dx = target.x - pos.x;

            double angle = Math.atan(dx / dz);


            Vec3d vec = new Vec3d(Math.cos(angle), 0, Math.sin(angle));

            return new Vec3dScriptObject(vec);
        });

    }


    private HexId parseAsAt(String command, int raisedPermission) {
        ServerCommandSource source = entity.getCommandSource()
                .withLevel(raisedPermission < 1 ? entity.getCommandSource().level : raisedPermission)
                .withEntity(entity)
                .withPosition(entity.getPos())
                .withSilent()
                .withRotation(entity.getRotationClient());

        var parsed = CommandApi.parseCommand(command, source);

        HexId hexid = HexId.makeNewUnique(ScriptManager.ScriptCaches.PREPARSED_COMMANDS.keySet());
        ScriptManager.ScriptCaches.PREPARSED_COMMANDS.put(hexid, parsed);
        return hexid;
    }

    private static double mag(Vec3d vec) {
        return Math.sqrt((vec.x * vec.x) + (vec.y * vec.y) + (vec.z * vec.z));
    }
}
