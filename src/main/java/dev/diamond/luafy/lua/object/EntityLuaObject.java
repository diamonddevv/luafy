package dev.diamond.luafy.lua.object;

import dev.diamond.luafy.lua.LuaTypeConversions;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.function.Function;

public class EntityLuaObject extends AbstractLuaObject {
    public final Entity entity;

    public EntityLuaObject(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void create() {
        this.set("get_name", new GetWithEntityFunc(e -> e.getName().getString()));
        this.set("get_uuid", new GetWithEntityFunc(Entity::getUuidAsString));
        this.set("get_nbt", new GetWithEntityFunc(e -> LuaTypeConversions.tableFromNbt(NbtPredicate.entityToNbt(e))));
        this.set("get_type", new GetWithEntityFunc(e -> e.getType().getName().getString()));

        this.set("as_player", new ToPlayerCastFunc());
    }

    private class GetWithEntityFunc extends AbstractLuaObject.GetFunc<Entity> {
        public GetWithEntityFunc(Function<Entity, Object> func) {
            super(func);
        }

        @Override
        public Entity get() {
            return entity;
        }
    }

    private class ToPlayerCastFunc extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            if (entity instanceof ServerPlayerEntity spe) {
                return new PlayerLuaObject(spe);
            } else return NIL;
        }
    }
}
