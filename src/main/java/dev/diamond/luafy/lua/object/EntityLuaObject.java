package dev.diamond.luafy.lua.object;

import dev.diamond.luafy.lua.LuaTypeConversions;
import dev.diamond.luafy.lua.LuafyLua;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
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

        this.set("is_player", new GetWithEntityFunc(e -> e instanceof ServerPlayerEntity));
        this.set("is_living", new GetWithEntityFunc(e -> e instanceof LivingEntity));

        this.set("test_predicate", new TestPredicateFunc());

        this.set("as_living", new ToLivingCastFunc());
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

    private class ToLivingCastFunc extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            if (entity instanceof LivingEntity e) {
                return new LivingEntityLuaObject(e);
            } else return NIL;
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

    private class TestPredicateFunc extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            return LuaValue.valueOf(LuafyLua.getAndTestPredicate(arg.checkjstring(), entity));
        }
    }
}
