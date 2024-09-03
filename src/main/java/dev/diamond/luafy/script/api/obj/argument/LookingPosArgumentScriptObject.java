package dev.diamond.luafy.script.api.obj.argument;

import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import net.minecraft.command.argument.LookingPosArgument;

public class LookingPosArgumentScriptObject extends AbstractTypedScriptObject<LookingPosArgument> implements CommandArgument {

    private final LookingPosArgument arg;

    public LookingPosArgumentScriptObject(LookingPosArgument arg) {
        this.arg = arg;
    }

    @Override
    public Object getArg() {
        return arg;
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {
        f.add_NoParams_Desc("get_x", args -> arg.x, "Gets the X component of this argument.", Number.class);
        f.add_NoParams_Desc("get_y", args -> arg.y, "Gets the Y component of this argument.", Number.class);
        f.add_NoParams_Desc("get_z", args -> arg.z, "Gets the Z component of this argument.", Number.class);

        f.add_Void_Desc("set_x", args -> { arg.x = args[0].asDouble(); return null; }, "Sets the X component of this argument.", new NamedParam("value", Number.class));
        f.add_Void_Desc("set_y", args -> { arg.y = args[0].asDouble(); return null; }, "Sets the Y component of this argument.", new NamedParam("value", Number.class));
        f.add_Void_Desc("set_z", args -> { arg.z = args[0].asDouble(); return null; }, "Sets the Z component of this argument.", new NamedParam("value", Number.class));
    }

    @Override
    public LookingPosArgument get() {
        return arg;
    }
}
