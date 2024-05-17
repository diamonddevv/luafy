package dev.diamond.luafy.script.old.object;

import dev.diamond.luafy.script.api.CommandApi;
import dev.diamond.luafy.script.lua.LuaHexid;
import dev.diamond.luafy.script.lua.LuaTypeConversions;
import dev.diamond.luafy.script.old.LuafyLua;
import dev.diamond.luafy.util.HexId;
import dev.diamond.luafy.util.LuafyUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
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

        this.set("parse_command_as_at", new ParseAsAtFunc());

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
            return LuaValue.valueOf(LuafyUtil.getAndTestPredicate(arg.checkjstring(), entity));
        }
    }

    public class ParseAsAtFunc extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg, LuaValue arg2) {
            ServerCommandSource source = entity.getCommandSource()
                    .withLevel(arg2.checkint())
                    .withEntity(entity)
                    .withPosition(entity.getPos())
                    .withRotation(entity.getRotationClient());

            String command = arg.checkjstring();
            var parsed = CommandApi.parseCommand(command, source);

            HexId hexid = HexId.makeNewUnique(LuafyLua.ScriptManagements.PREPARSED_COMMANDS_CACHE.keySet());
            LuafyLua.ScriptManagements.PREPARSED_COMMANDS_CACHE.put(hexid, parsed);
            return new LuaHexid(hexid);
        }
    }
}
