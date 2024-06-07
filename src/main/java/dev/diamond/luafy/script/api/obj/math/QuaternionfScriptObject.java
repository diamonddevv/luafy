package dev.diamond.luafy.script.api.obj.math;

import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import org.joml.Quaternionf;

import java.util.HashMap;

public class QuaternionfScriptObject implements IScriptObject {

    private final Quaternionf quaternion;

    public QuaternionfScriptObject(Quaternionf quaternion) {
        this.quaternion = quaternion;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        set.put("get_x", args -> quaternion.x);
        set.put("get_y", args -> quaternion.y);
        set.put("get_z", args -> quaternion.z);
        set.put("get_w", args -> quaternion.w);

        set.put("set_x", args -> quaternion.x = args[0].asFloat());
        set.put("set_y", args -> quaternion.y = args[0].asFloat());
        set.put("set_z", args -> quaternion.z = args[0].asFloat() );
        set.put("set_w", args -> quaternion.w = args[0].asFloat());
    }

    public Quaternionf get() {
        return quaternion;
    }
}
