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
        f.add_NoParams_Desc("get_x_relative", args -> arg.isXRelative(), "Returns true if this arguments X component is relative (Uses tildes [~])", Boolean.class);
        f.add_NoParams_Desc("get_y_relative", args -> arg.isYRelative(), "Returns true if this arguments Y component is relative (Uses tildes [~])", Boolean.class);
        f.add_NoParams_Desc("get_z_relative", args -> arg.isZRelative(), "Returns true if this arguments Z component is relative (Uses tildes [~])", Boolean.class);

        f.add_Void_Desc("set_x_relative", args -> { arg.x.relative = args[0].asBoolean(); return null; }, "Sets the whether this arguments X component is relative (Uses tildes [~])", new NamedParam("relative", Boolean.class));
        f.add_Void_Desc("set_y_relative", args -> { arg.y.relative = args[0].asBoolean(); return null; }, "Sets the whether this arguments Y component is relative (Uses tildes [~])", new NamedParam("relative", Boolean.class));
        f.add_Void_Desc("set_z_relative", args -> { arg.z.relative = args[0].asBoolean(); return null; }, "Sets the whether this arguments Z component is relative (Uses tildes [~])", new NamedParam("relative", Boolean.class));

        f.add_NoParams_Desc("get_x", args -> arg.x.value, "Gets the X component of this argument.", Number.class);
        f.add_NoParams_Desc("get_y", args -> arg.y.value, "Gets the Y component of this argument.", Number.class);
        f.add_NoParams_Desc("get_z", args -> arg.z.value, "Gets the Z component of this argument.", Number.class);

        f.add_Void_Desc("set_x", args -> { arg.x.value = args[0].asDouble(); return null; }, "Sets the X component of this argument.", new NamedParam("value", Number.class));
        f.add_Void_Desc("set_y", args -> { arg.x.value = args[0].asDouble(); return null; }, "Sets the Y component of this argument.", new NamedParam("value", Number.class));
        f.add_Void_Desc("set_z", args -> { arg.x.value = args[0].asDouble(); return null; }, "Sets the Z component of this argument.", new NamedParam("value", Number.class));
    }

    @Override
    public DefaultPosArgument get() {
        return arg;
    }
}
