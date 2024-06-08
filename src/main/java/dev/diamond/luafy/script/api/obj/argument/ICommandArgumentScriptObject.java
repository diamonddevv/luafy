package dev.diamond.luafy.script.api.obj.argument;

import dev.diamond.luafy.script.abstraction.BaseValueAdapter;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import net.minecraft.command.argument.DefaultPosArgument;
import net.minecraft.command.argument.LookingPosArgument;

public interface ICommandArgumentScriptObject {
    static AbstractBaseValue<?, ?> adapt(Object o, BaseValueAdapter adapter) {

        if (o instanceof DefaultPosArgument dpa)
            return adapter.adapt(new DefaultPosArgumentScriptObject(dpa));
        else if (o instanceof LookingPosArgument lpa)
            return adapter.adapt(new LookingPosArgumentScriptObject(lpa));

        return adapter.adapt(o);
    }

    Object getArg();
}
