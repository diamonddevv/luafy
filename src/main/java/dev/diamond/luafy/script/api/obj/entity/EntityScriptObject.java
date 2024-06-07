package dev.diamond.luafy.script.api.obj.entity;

import dev.diamond.luafy.cca.LuafyCCA;
import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.BaseValueConversions;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import dev.diamond.luafy.script.api.CommandApi;
import dev.diamond.luafy.script.api.obj.entity.display.DisplayEntityScriptObject;
import dev.diamond.luafy.script.api.obj.math.Vec3dScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.WorldScriptObject;
import dev.diamond.luafy.script.nbt.OptionallyExplicitNbtElement;
import dev.diamond.luafy.util.HexId;
import dev.diamond.luafy.util.LuafyUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
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
        set.put("get_facing_vector", args -> {
            var yaw = entity.getYaw(); // xz
            var pitch = entity.getPitch(); // y


            if (yaw < 0) yaw = 360 + yaw;
            if (pitch < 0) pitch = 360 + pitch;

            return new Vec3dScriptObject(new Vec3d(Math.cos(Math.toRadians(yaw + 90)), Math.sin(Math.toRadians(pitch + 90)), Math.sin(Math.toRadians(yaw + 90))));
        });


        set.put("parse_command_as_at", args -> parseAsAt(args[0].asString(), args[1] == null ? 0 : args[1].asInt()));

        set.put("is_living", args -> entity instanceof LivingEntity);
        set.put("is_player", args -> entity instanceof ServerPlayerEntity);
        set.put("is_type", args -> entity.getType() == Registries.ENTITY_TYPE.get(new Identifier(args[0].asString())));

        set.put("test_predicate", args -> LuafyUtil.getAndTestPredicate(args[0].asString(), entity));

        set.put("as_player", args -> new PlayerEntityScriptObject((ServerPlayerEntity) entity));
        set.put("as_living", args -> new LivingEntityScriptObject((LivingEntity) entity));
        set.put("as_display", args -> new DisplayEntityScriptObject((DisplayEntity) entity));

        set.put("teleport", args -> {
            Vec3d pos = args[0].asScriptObjectAssertive(Vec3dScriptObject.class).get();
            entity.teleport(pos.x, pos.y, pos.z);
            return null;
        });

        set.put("get_world", args -> new WorldScriptObject(entity.getServer().getWorld(entity.getWorld().getRegistryKey())));

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

            return new Vec3dScriptObject(angleTo(pos, target));
        });

        set.put("angle_towards_entity", args -> {
            Vec3d pos = entity.getPos();
            Vec3d target = args[0].asScriptObjectAssertive(EntityScriptObject.class).entity.getPos();

            return new Vec3dScriptObject(angleTo(pos, target));
        });

        // cca
        set.put("get_cca_nbt", args -> BaseValueConversions.implicit_nbtToBase(LuafyCCA.EntityScriptDataManager.get(entity, args[0].asString()), args[0]::adapt));
        set.put("set_cca_nbt", args -> {
            LuafyCCA.EntityScriptDataManager.set(entity, args[0].asString(),
                    BaseValueConversions.mapToCompound((HashMap<AbstractBaseValue<?, ?>, AbstractBaseValue<?, ?>>) args[1].asMap()));
            return null;
        });
        set.put("modify_cca_nbt", args -> {
            String subkey = args[0].asString();
            AdaptableFunction function = args[1].asFunction();

            var nbt = LuafyCCA.EntityScriptDataManager.get(entity, subkey);
            var r = function.call(BaseValueConversions.implicit_nbtToBase(nbt, args[0]::adapt));
            nbt = BaseValueConversions.mapToCompound((HashMap<AbstractBaseValue<?, ?>, AbstractBaseValue<?, ?>>) args[0].adapt(r).asMap());
            LuafyCCA.EntityScriptDataManager.set(entity, subkey, nbt);

            return null;
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

    private static Vec3d angleTo(Vec3d pos, Vec3d target) {
        double dz = target.z - pos.z;
        double dx = target.x - pos.x;
        double hyp = Math.sqrt((dx * dx) + (dz * dz));
        double angleHor = Math.atan2(dz, dx);


        double dy = target.y - pos.y;
        double angleVer = Math.atan2(dy, hyp);

        return new Vec3d(Math.cos(angleHor), Math.sin(angleVer), Math.sin(angleHor));
    }
}
