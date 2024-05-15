package dev.diamond.luafy.script.old.api;

import dev.diamond.luafy.script.old.Old_LuaScript;
import dev.diamond.luafy.script.old.LuafyLua;
import dev.diamond.luafy.script.old.object.EntityLuaObject;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class ContextApi extends AbstractApi {
    private final Old_LuaScript script;

    public ContextApi(Old_LuaScript script) {
        super("context");
        this.script = script;
    }

    @Override
    public void create(LuaTable table) {
        table.set("get", new GetContextFunc());
        table.set("luacall", new CallLuaFunc());
        table.set("set_outctx", new SetReturnContextFunc());
    }


    public class GetContextFunc extends ZeroArgFunction {

        @Override
        public LuaValue call() {
            if (script.context != null) {
                return script.context;
            } else return NIL;
        }
    }

    public class CallLuaFunc extends ThreeArgFunction {

        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2, @Nullable LuaValue arg3Optional) {
            String lua = arg1.checkjstring();
            LuaTable ctx = arg2.checktable();
            EntityLuaObject entityLuaObject = (EntityLuaObject) arg3Optional;

            Old_LuaScript s = LuafyLua.LUA_SCRIPTS.get(lua);

            ServerCommandSource src = entityLuaObject == null ? script.source :
                    script.source
                            .withEntity(entityLuaObject.entity)
                            .withPosition(entityLuaObject.entity.getPos())
                            .withRotation(entityLuaObject.entity.getRotationClient());

            s.execute(src, ctx);

            return s.outContext == LuaValue.NIL ? LuaValue.NIL : s.outContext;
        }
    }

    public class SetReturnContextFunc extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            script.outContext = arg.checktable();
            return NIL;
        }
    }
}
