package dev.diamond.luafy.script.api.obj.argument;

import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import net.minecraft.command.argument.DefaultPosArgument;

import java.util.HashMap;

public class DefaultPosArgumentScriptObject implements ICommandArgumentScriptObject {

    private final DefaultPosArgument arg;

    public DefaultPosArgumentScriptObject(DefaultPosArgument arg) {
        this.arg = arg;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        set.put("get_x", args -> arg.x.value);
        set.put("get_y", args -> arg.y.value);
        set.put("get_z", args -> arg.z.value);

        set.put("set_x", args -> { arg.x.value = args[0].asDouble(); return null; });
        set.put("set_y", args -> { arg.y.value = args[0].asDouble(); return null; });
        set.put("set_z", args -> { arg.z.value = args[0].asDouble(); return null; });
    }
}
