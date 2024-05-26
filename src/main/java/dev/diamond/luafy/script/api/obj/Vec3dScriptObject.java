package dev.diamond.luafy.script.api.obj;

import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;

public class Vec3dScriptObject implements IScriptObject {

    private final Vec3d vec;

    public Vec3dScriptObject(Vec3d vec) {
        this.vec = vec;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        set.put("get_x", args -> vec.x);
        set.put("get_y", args -> vec.y);
        set.put("get_z", args -> vec.z);
    }
}
