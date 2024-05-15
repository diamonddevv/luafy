package dev.diamond.luafy.script.old.api;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.BaseLib;

public abstract class AbstractApi extends BaseLib {

    public AbstractApi(String name) {
        this.name = name;
    }

    public abstract void create(LuaTable table);

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable table = new LuaTable();
        create(table);
        registerApi(env, this.name, table);
        return NIL;
    }


    public static void registerApi(LuaValue env, String name, LuaTable table) {
        env.set(name, table);
        if (!env.get("package").isnil())
            env.get("package").get("loaded").set(name, table);
    }
}
