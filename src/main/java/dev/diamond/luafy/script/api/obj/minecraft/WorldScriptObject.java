package dev.diamond.luafy.script.api.obj.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import dev.diamond.luafy.script.api.obj.entity.EntityScriptObject;
import dev.diamond.luafy.script.api.obj.math.Vec3dScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.block.BlockStateScriptObject;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Objects;
import java.util.UUID;

public class WorldScriptObject extends AbstractTypedScriptObject<ServerWorld> {

    private final ServerWorld world;

    public WorldScriptObject(ServerWorld world) {
        this.world = world;
    }

    public ServerWorld get() {
        return world;
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {
        f.add_Desc("get_blockstate", args -> {
            Vec3d vec = args[0].asScriptObjectAssertive(Vec3dScriptObject.class).get();
            return new BlockStateScriptObject(world.getBlockState(BlockPos.ofFloored(vec.x, vec.y, vec.z)));
        }, "Gets the blockstate at this position (floored)", BlockStateScriptObject.class, new NamedParam("pos", Vec3dScriptObject.class));

        f.add_OptionalParams_Desc("spawn_entity", args -> {
            String entityId = args[0].asString();
            Vec3d pos = args[1].asScriptObjectAssertive(Vec3dScriptObject.class).get();
            String snbt = null;
            if (args.length > 2) snbt = args[2].asString();

            NbtCompound nbt = new NbtCompound();
            if (snbt != null) {
                try {
                    nbt = StringNbtReader.parse(snbt);
                } catch (CommandSyntaxException e) {
                    Luafy.LOGGER.warn("Could not read SNBT: " + e);
                    nbt = new NbtCompound();
                }
            }


            var entityType = Registries.ENTITY_TYPE.get(Identifier.of(entityId));

            Entity e = entityType.create(world);


            e.setPos(pos.x, pos.y, pos.z);
            e.setYaw(0);
            e.setPitch(0);

            NbtCompound store = new NbtCompound();
            e.writeNbt(store);

            for (String s : nbt.getKeys()) {
                store.put(s, nbt.get(s));
            }

            e.readNbt(store); // this makes no sense, why do you "read" to set it?? mojank moment
            world.spawnEntity(e);

            return new EntityScriptObject(e);
        }, "Spawns the specified entity at the specified position. Optionally takes in entity NBT to add to the entity.",
                EntityScriptObject.class,
                new NamedParam[] {new NamedParam("snbt", String.class)},
                new NamedParam("entityId", String.class),
                new NamedParam("pos", Vec3dScriptObject.class));

        f.add_Desc("get_entity_from_uuid", args -> {
            UUID uuid = UUID.fromString(args[0].asString());
            var e = world.getEntity(uuid);
            return new EntityScriptObject(e);
        }, "Gets the entity with this UUID.", EntityScriptObject.class, new NamedParam("uuid", String.class));


        f.add_NoParams_Desc("get_time_of_day", args -> world.getTimeOfDay(), "Gets the current time of day.", Number.class);

        f.add_Void_Desc("play_sound", args -> {
            Vec3d pos = args[0].asScriptObjectAssertive(Vec3dScriptObject.class).get();
            Identifier soundId = Identifier.of(args[1].asString());
            String category = args[2].asString();
            float pitch = args[3].asFloat();
            float volume = args[4].asFloat();

            SoundEvent sound = Registries.SOUND_EVENT.get(soundId);

            world.playSound(
                    null,
                    pos.x, pos.y, pos.z,
                    sound,
                    !Objects.equals(category, "") ? SoundCategory.MASTER : SoundCategory.valueOf(category),
                    volume, pitch
            );


            return null;
        },
                "Plays the specified sound to all players at the specified position with the specified sound category, pitch and volume.",
                new NamedParam("pos", Vec3dScriptObject.class),
                new NamedParam("soundId", String.class),
                new NamedParam("soundCategory", String.class),
                new NamedParam("pitch", Number.class),
                new NamedParam("volume", Number.class));

        f.add_Desc("raycast", args -> {

            Vec3d from = args[0].asScriptObjectAssertive(Vec3dScriptObject.class).get();
            Vec3d direction = args[1].asScriptObjectAssertive(Vec3dScriptObject.class).get();
            float dist = args[2].asFloat();
            boolean blocks = args[3].asBoolean();
            boolean entities = args[4].asBoolean();


            Vec3d endPosition = from.add(direction.multiply(dist));

            if (blocks) {
                var result = world.raycast(new RaycastContext(from, endPosition, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, ShapeContext.absent()));
                if (result.getType() == HitResult.Type.BLOCK) {
                    return true;
                }
            }

            if (entities) {
                for (float d = 0; d > dist; d += 0.1f) {
                    Vec3d cp = from.add(direction.multiply(d));

                    var collisions = world.getEntityCollisions(null, new Box(cp, cp)); // check at a point
                    if (!collisions.isEmpty()) return true;
                }
            }

            return false;

        }, "Calculates a raycast from point `origin`. Returns true if collision was detected. `check_blocks` and `check_entities` can be used to control collisions.<br>" +
                        "`direction` is a vector representing the direction of the raycast.<br>" +
                        "This direction can be found easily in the form (x, y, z): (`cos(yaw)`, `sin(yaw)`, `sin(pitch)`)<br><br>" +
                        "finalRaycastPosition = `origin` + (`direction` * `distance`)",
                Boolean.class,
                new NamedParam("origin", Vec3dScriptObject.class),
                new NamedParam("direction", Vec3dScriptObject.class),
                new NamedParam("distance", Vec3dScriptObject.class),
                new NamedParam("check_blocks", Boolean.class),
                new NamedParam("check_entities", Boolean.class));
    }
}
