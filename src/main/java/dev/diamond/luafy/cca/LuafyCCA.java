package dev.diamond.luafy.cca;

import dev.diamond.luafy.Luafy;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

import java.util.function.Consumer;

public class LuafyCCA implements EntityComponentInitializer {

    public static final ComponentKey<EntityCompound> SCRIPT_DATA =
            ComponentRegistryV3.INSTANCE.getOrCreate(Luafy.id("shared"), EntityCompound.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, SCRIPT_DATA, e -> new EntityCompound("data", new NbtCompound()));
    }




    public static class EntityScriptDataManager {
        public static NbtCompound get(Entity e, String subkey) {
            var c = SCRIPT_DATA.get(e).get().getCompound(subkey);
            if (c == null) {
                set(e, subkey, new NbtCompound());
                return get(e, subkey);
            }
            return c;
        }

        public static void set(Entity e, String subkey, NbtCompound compound) {
            var nbt = SCRIPT_DATA.get(e).get();
            nbt.put(subkey, compound);
            SCRIPT_DATA.get(e).set(nbt);
        }

        public static void modify(Entity e, String subkey, Consumer<NbtCompound> c) {
            var nbt = get(e, subkey);
            c.accept(nbt);
            set(e, subkey, nbt);
        }
    }
}
