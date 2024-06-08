package dev.diamond.luafy.script.api.obj.math;

import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import net.minecraft.util.math.Vec3d;

import java.util.function.Function;

public class Vec3dScriptObject extends AbstractTypedScriptObject<Vec3d> {

    private final Vec3d vec;

    public Vec3dScriptObject(Vec3d vec) {
        this.vec = vec;
    }

    private Object withOther(AbstractBaseValue<?, ?>[] args, Function<Vec3d, Object> f) {
        Vec3dScriptObject vecSo = null;

        if (args[0].isScriptObjectAssertive(Vec3dScriptObject.class)) {
            vecSo = args[0].asScriptObjectAssertive(Vec3dScriptObject.class);
        }

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

    @Override
    public void getTypedFunctions(TypedFunctionList f) {

        f.add_NoParams("get_x", args -> vec.x, Number.class);
        f.add_NoParams("get_y", args -> vec.y, Number.class);
        f.add_NoParams("get_z", args -> vec.z, Number.class);

        f.add("add",          args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::add)), Vec3dScriptObject.class, new NamedParam("other", Vec3dScriptObject.class));
        f.add("multiply",     args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::multiply)), Vec3dScriptObject.class, new NamedParam("other", Vec3dScriptObject.class));
        f.add("distance",     args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::distanceTo)), Vec3dScriptObject.class, new NamedParam("other", Vec3dScriptObject.class));
        f.add("sqr_distance", args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::squaredDistanceTo)), Vec3dScriptObject.class, new NamedParam("other", Vec3dScriptObject.class));
        f.add("dot",          args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::dotProduct)), Vec3dScriptObject.class, new NamedParam("other", Vec3dScriptObject.class));
        f.add("cross",        args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::crossProduct)), Vec3dScriptObject.class, new NamedParam("other", Vec3dScriptObject.class));
        f.add("relativize",   args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::relativize)), Vec3dScriptObject.class, new NamedParam("other", Vec3dScriptObject.class));
        f.add("lerp",         args -> new Vec3dScriptObject((Vec3d) withOther(args, v -> vec.lerp(v, args[2].asDouble()))), Vec3dScriptObject.class, new NamedParam("other", Vec3dScriptObject.class));

        f.add_NoParams("normalize", args -> new Vec3dScriptObject(vec.normalize()), Vec3dScriptObject.class);
        f.add_NoParams("negate", args -> new Vec3dScriptObject(vec.negate()), Vec3dScriptObject.class);

        f.add_NoParams("to_string", args -> vec.toString(), String.class);
    }
}
