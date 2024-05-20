package dev.diamond.luafy.script.lua;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

public abstract class ArrArgFunction extends LibFunction {
    public abstract LuaValue call(LuaValue[] params);

    public ArrArgFunction() {}

    @Override
    public final LuaValue call() {
        return call(NIL, NIL, NIL, NIL);
    }

    @Override
    public final LuaValue call(LuaValue a) {
        return call(a, NIL, NIL, NIL);
    }

    @Override
    public LuaValue call(LuaValue a, LuaValue b) {
        return call(a, b, NIL, NIL);
    }

    @Override
    public LuaValue call(LuaValue a, LuaValue b, LuaValue c) {
        return call(a, b, c, NIL);
    }

    @Override
    public LuaValue call(LuaValue a, LuaValue b, LuaValue c, LuaValue d) {
        return call(new LuaValue[] { a, b, c, d });
    }

    @Override
    public Varargs invoke(Varargs args) {

        LuaValue[] arr = new LuaValue[args.narg()];
        for (int i = 0; i < args.narg(); i++) {
            arr[i] = args.arg(i + 1);
        }

        return call(arr);
    }
}
