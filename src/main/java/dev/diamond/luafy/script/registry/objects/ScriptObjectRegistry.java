package dev.diamond.luafy.script.registry.objects;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.api.obj.argument.DefaultPosArgumentScriptObject;
import dev.diamond.luafy.script.api.obj.argument.LookingPosArgumentScriptObject;
import dev.diamond.luafy.script.api.obj.entity.EntityScriptObject;
import dev.diamond.luafy.script.api.obj.entity.LivingEntityScriptObject;
import dev.diamond.luafy.script.api.obj.entity.PlayerEntityScriptObject;
import dev.diamond.luafy.script.api.obj.entity.display.DisplayEntityScriptObject;
import dev.diamond.luafy.script.api.obj.entity.display.ItemDisplayEntityScriptObject;
import dev.diamond.luafy.script.api.obj.entity.display.TextDisplayEntityScriptObject;
import dev.diamond.luafy.script.api.obj.math.Matrix4fScriptObject;
import dev.diamond.luafy.script.api.obj.math.QuaternionfScriptObject;
import dev.diamond.luafy.script.api.obj.math.Vec3dScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.WorldScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.block.BlockStatePropertyScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.block.BlockStateScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.item.ComponentMapScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.item.ItemStackScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.item.MapStateScriptObject;
import dev.diamond.luafy.script.api.obj.util.AdvancementEntryScriptObject;
import dev.diamond.luafy.script.api.obj.util.BufferedImageScriptObject;
import dev.diamond.luafy.script.api.obj.util.ByteBufScriptObject;
import dev.diamond.luafy.script.api.obj.util.StorageScriptObject;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.DefaultPosArgument;
import net.minecraft.command.argument.LookingPosArgument;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.awt.image.BufferedImage;
import java.util.Optional;

import static dev.diamond.luafy.script.registry.objects.ScriptObjectFactory.of;

public class ScriptObjectRegistry {

    public static final ScriptObjectFactory<DefaultPosArgumentScriptObject> ARG_DEFAULTPOS = of(DefaultPosArgumentScriptObject.class, a -> new DefaultPosArgumentScriptObject((DefaultPosArgument) a[0]));
    public static final ScriptObjectFactory<LookingPosArgumentScriptObject> ARG_LOOKINGPOS = of(LookingPosArgumentScriptObject.class, a -> new LookingPosArgumentScriptObject((LookingPosArgument) a[0]));

    public static final ScriptObjectFactory<EntityScriptObject> ENTITY = of(EntityScriptObject.class, a -> new EntityScriptObject((Entity) a[0]));
    public static final ScriptObjectFactory<LivingEntityScriptObject> LIVING_ENTITY = of(LivingEntityScriptObject.class, a -> new LivingEntityScriptObject((LivingEntity) a[0]));
    public static final ScriptObjectFactory<PlayerEntityScriptObject> PLAYER_ENTITY = of(PlayerEntityScriptObject.class, a -> new PlayerEntityScriptObject((ServerPlayerEntity) a[0]));
    public static final ScriptObjectFactory<DisplayEntityScriptObject> DISPLAY_ENTITY = of(DisplayEntityScriptObject.class, a -> new DisplayEntityScriptObject((DisplayEntity) a[0]));
    public static final ScriptObjectFactory<ItemDisplayEntityScriptObject> ITEM_DISPLAY_ENTITY = of(ItemDisplayEntityScriptObject.class, a -> new ItemDisplayEntityScriptObject((DisplayEntity.ItemDisplayEntity) a[0]));
    public static final ScriptObjectFactory<TextDisplayEntityScriptObject> TEXT_DISPLAY_ENTITY = of(TextDisplayEntityScriptObject.class, a -> new TextDisplayEntityScriptObject((DisplayEntity.TextDisplayEntity) a[0]));

    public static final ScriptObjectFactory<Matrix4fScriptObject> MATRIX = of(Matrix4fScriptObject.class, a -> new Matrix4fScriptObject((Matrix4f) a[0]));
    public static final ScriptObjectFactory<QuaternionfScriptObject> QUATERNION = of(QuaternionfScriptObject.class, a -> new QuaternionfScriptObject((Quaternionf) a[0]));
    public static final ScriptObjectFactory<Vec3dScriptObject> VEC3D = of(Vec3dScriptObject.class, a -> new Vec3dScriptObject((Vec3d) a[0]));

    public static final ScriptObjectFactory<BlockStatePropertyScriptObject> BLOCKSTATE_PROPERTY = of(BlockStatePropertyScriptObject.class, a -> new BlockStatePropertyScriptObject((Property<?>) a[0]));
    public static final ScriptObjectFactory<BlockStateScriptObject> BLOCKSTATE = of(BlockStateScriptObject.class, a -> new BlockStateScriptObject((BlockState) a[0]));
    public static final ScriptObjectFactory<ItemStackScriptObject> ITEMSTACK = of(ItemStackScriptObject.class, a -> new ItemStackScriptObject((ItemStack) a[0]));
    public static final ScriptObjectFactory<MapStateScriptObject> MAP_STATE = of(MapStateScriptObject.class, a -> new MapStateScriptObject((MapState) a[0]));
    public static final ScriptObjectFactory<WorldScriptObject> WORLD = of(WorldScriptObject.class, a -> new WorldScriptObject((ServerWorld) a[0]));
    public static final ScriptObjectFactory<ComponentMapScriptObject> COMPONENTMAP = of(ComponentMapScriptObject.class, a -> new ComponentMapScriptObject((ComponentMap) a[0]));

    public static final ScriptObjectFactory<AdvancementEntryScriptObject> ADVANCEMENT_ENTRY = of(AdvancementEntryScriptObject.class, a -> new AdvancementEntryScriptObject((AdvancementEntry) a[0]));
    public static final ScriptObjectFactory<BufferedImageScriptObject> BUFFERED_IMAGE = of(BufferedImageScriptObject.class, a -> new BufferedImageScriptObject((BufferedImage) a[0]));
    public static final ScriptObjectFactory<ByteBufScriptObject> BYTEBUF = of(ByteBufScriptObject.class, a -> new ByteBufScriptObject((byte[]) a[0]));
    public static final ScriptObjectFactory<StorageScriptObject> STORAGE = of(StorageScriptObject.class, a -> new StorageScriptObject((ServerCommandSource) a[0], (Identifier) a[1]));



    public static void registerAll() {
        register(ARG_DEFAULTPOS, Luafy.id("default_pos"));
        register(ARG_LOOKINGPOS, Luafy.id("looking_pos"));
        register(ENTITY, Luafy.id("entity"));
        register(LIVING_ENTITY, Luafy.id("living_entity"));
        register(PLAYER_ENTITY, Luafy.id("player_entity"));
        register(DISPLAY_ENTITY, Luafy.id("display_entity"));
        register(ITEM_DISPLAY_ENTITY, Luafy.id("item_display_entity"));
        register(TEXT_DISPLAY_ENTITY, Luafy.id("text_display_entity"));
        register(MATRIX, Luafy.id("matrix"));
        register(QUATERNION, Luafy.id("quaternion"));
        register(VEC3D, Luafy.id("vec3d"));
        register(BLOCKSTATE_PROPERTY, Luafy.id("blockstate_property"));
        register(BLOCKSTATE, Luafy.id("blockstate"));
        register(ITEMSTACK, Luafy.id("item_stack"));
        register(MAP_STATE, Luafy.id("map_state"));
        register(WORLD, Luafy.id("world"));
        register(COMPONENTMAP, Luafy.id("component_map"));
        register(ADVANCEMENT_ENTRY, Luafy.id("advancement_entry"));
        register(BUFFERED_IMAGE, Luafy.id("image"));
        register(BYTEBUF, Luafy.id("bytebuf"));
        register(STORAGE, Luafy.id("nbt_storage"));
    }

    private static void register(ScriptObjectFactory<?> factory, Identifier id) {
        Registry.register(Luafy.Registries.SCRIPT_OBJECTS, id, factory);
    }

    public static Optional<ScriptObjectFactory<?>> getByClass(Class<?> clazz) {
        return Luafy.Registries.SCRIPT_OBJECTS.stream().filter(factory -> factory.representedClass() == clazz).findFirst();
    }
}
