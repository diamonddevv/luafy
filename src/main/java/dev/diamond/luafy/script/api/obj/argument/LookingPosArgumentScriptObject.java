package dev.diamond.luafy.script.api.obj.argument;

import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import net.minecraft.command.argument.LookingPosArgument;

public class LookingPosArgumentScriptObject extends AbstractTypedScriptObject<LookingPosArgument> implements ICommandArgumentScriptObject {

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
        f.add_NoParams("get_x", args -> arg.x, Number.class);
        f.add_NoParams("get_y", args -> arg.y, Number.class);
        f.add_NoParams("get_z", args -> arg.z, Number.class);

        f.add_Void("set_x", args -> { arg.x = args[0].asDouble(); return null; }, new NamedParam("value", Number.class));
        f.add_Void("set_y", args -> { arg.y = args[0].asDouble(); return null; }, new NamedParam("value", Number.class));
        f.add_Void("set_z", args -> { arg.z = args[0].asDouble(); return null; }, new NamedParam("value", Number.class));
    }

    @Override
    public LookingPosArgument get() {
        return arg;
    }
}
