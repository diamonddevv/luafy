package dev.diamond.luafy.script.api.obj.math;

import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.function.Function;

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


        set.put("add",      args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::add)));
        set.put("multiply", args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::multiply)));
        set.put("distance", args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::distanceTo)));
        set.put("dot",      args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::dotProduct)));
        set.put("cross",    args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::crossProduct)));


        set.put("to_string", args -> vec.toString());
    }

    private Object withOther(AbstractBaseValue<?, ?>[] args, Function<Vec3d, Object> f) {
        var vecSo = args[0].asScriptObjectAssertive(Vec3dScriptObject.class);

        Vec3d vec;
        if (vecSo == null) {
            vec = new Vec3d(args[0].asDouble(), args[1].asDouble(), args[2].asDouble());
        } else {
            vec = vecSo.get();
        }

        return f.apply(vec);
    }

    public Vec3d get() {
        return vec;
    }
}
