package dev.diamond.luafy.script.api.obj.argument;

import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.command.argument.DefaultPosArgument;
import net.minecraft.command.argument.LookingPosArgument;

import java.util.HashMap;

public class LookingPosArgumentScriptObject implements ICommandArgumentScriptObject {

    private final LookingPosArgument arg;

    public LookingPosArgumentScriptObject(LookingPosArgument arg) {
        this.arg = arg;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        set.put("get_x", args -> arg.x);
        set.put("get_y", args -> arg.y);
        set.put("get_z", args -> arg.z);

        set.put("set_x", args -> { arg.x = args[0].asDouble(); return null; });
        set.put("set_y", args -> { arg.y = args[0].asDouble(); return null; });
        set.put("set_z", args -> { arg.z = args[0].asDouble(); return null; });
    }

    @Override
    public Object getArg() {
        return arg;
    }
}
