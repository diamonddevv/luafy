package dev.diamond.luafy.script.api.obj.argument;

import dev.diamond.luafy.script.abstraction.BaseValueAdapter;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import net.minecraft.command.argument.DefaultPosArgument;

public interface ICommandArgumentScriptObject extends IScriptObject {
    static AbstractBaseValue<?, ?> adapt(Object o, BaseValueAdapter adapter) {
        if (o instanceof DefaultPosArgument dpa) {
            return adapter.adapt(new DefaultPosArgumentScriptObject(dpa));
        }

        return adapter.adapt(o);
    }
}
