package dev.diamond.luafy.script.api.obj.argument;

import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import net.minecraft.command.argument.DefaultPosArgument;

public class DefaultPosArgumentScriptObject extends AbstractTypedScriptObject<DefaultPosArgument> implements ICommandArgumentScriptObject {

    private final DefaultPosArgument arg;

    public DefaultPosArgumentScriptObject(DefaultPosArgument arg) {
        this.arg = arg;
    }

    @Override
    public Object getArg() {
        return arg;
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {
        f.add_NoParams("get_x_relative", args -> arg.isXRelative(), Boolean.class);
        f.add_NoParams("get_y_relative", args -> arg.isYRelative(), Boolean.class);
        f.add_NoParams("get_z_relative", args -> arg.isZRelative(), Boolean.class);

        f.add_Void("set_x_relative", args -> { arg.x.relative = args[0].asBoolean(); return null; }, new NamedParam("relative", Boolean.class));
        f.add_Void("set_y_relative", args -> { arg.y.relative = args[0].asBoolean(); return null; }, new NamedParam("relative", Boolean.class));
        f.add_Void("set_z_relative", args -> { arg.z.relative = args[0].asBoolean(); return null; }, new NamedParam("relative", Boolean.class));

        f.add_NoParams("get_x", args -> arg.x.value, Number.class);
        f.add_NoParams("get_y", args -> arg.y.value, Number.class);
        f.add_NoParams("get_z", args -> arg.z.value, Number.class);

        f.add_Void("set_x", args -> { arg.x.value = args[0].asDouble(); return null; }, new NamedParam("value", Number.class));
        f.add_Void("set_y", args -> { arg.x.value = args[0].asDouble(); return null; }, new NamedParam("value", Number.class));
        f.add_Void("set_z", args -> { arg.x.value = args[0].asDouble(); return null; }, new NamedParam("value", Number.class));
    }

    @Override
    public DefaultPosArgument get() {
        return arg;
    }
}
