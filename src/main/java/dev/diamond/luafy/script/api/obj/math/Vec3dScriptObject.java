package dev.diamond.luafy.script.api.obj.math;

import dev.diamond.luafy.script.abstraction.NamedParam;
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

        return withOther(vec, f);
    }

    private Object withOther(Vec3d vec, Function<Vec3d, Object> f) {
        return f.apply(vec);
    }

    public Vec3d get() {
        return vec;
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {

        f.add_NoParams_Desc("get_x", args -> vec.x, "Gets the X component of the vector.", Number.class);
        f.add_NoParams_Desc("get_y", args -> vec.y, "Gets the Y component of the vector.", Number.class);
        f.add_NoParams_Desc("get_z", args -> vec.z, "Gets the Z component of the vector.", Number.class);

        f.add_Desc("add",          args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::add)), "Returns the sum of this vector and another. [sum = other + this]", Vec3dScriptObject.class, new NamedParam("other", Vec3dScriptObject.class));
        f.add_Desc("multiply",     args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::multiply)), "Returns the product of this vector and another. [product = other * this]",  Vec3dScriptObject.class, new NamedParam("other", Vec3dScriptObject.class));
        f.add_Desc("multiply_u",   args -> new Vec3dScriptObject((Vec3d) withOther(new Vec3d(1, 1, 1).multiply(args[0].asFloat()), vec::multiply)), "Returns the product of this vector and a uniform vector of `f`. [product = other * (`f`, `f`, `f`)]",  Vec3dScriptObject.class, new NamedParam("f", Number.class));
        f.add_Desc("distance",     args ->                               withOther(args, vec::distanceTo), "Returns the mathematical distance from this vector to another. [distance = sqrt( (other.x - this.x)^2 + (other.y - this.y)^2 + (other.z - this.z)^2 )]", Number.class, new NamedParam("other", Vec3dScriptObject.class));
        f.add_Desc("sqr_distance", args ->                               withOther(args, vec::squaredDistanceTo), "Returns the mathematical distance squared from this vector to another. Runs faster than 'distance_to'. [distance = (other.x - this.x)^2 + (other.y - this.y)^2 + (other.z - this.z)^2]",  Number.class, new NamedParam("other", Vec3dScriptObject.class));
        f.add_Desc("dot",          args ->                               withOther(args, vec::dotProduct), "Returns the dot product of this vector to another. [dotProd = (this.x * other.x) + (this.y * other.y) + (this.z * other.z)]", Number.class, new NamedParam("other", Vec3dScriptObject.class));
        f.add_Desc("cross",        args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::crossProduct)), "Returns the cross product vector of this vector to another.", Vec3dScriptObject.class, new NamedParam("other", Vec3dScriptObject.class));
        f.add_Desc("relativize",   args -> new Vec3dScriptObject((Vec3d) withOther(args, vec::relativize)), "Returns a vector representing this vector relative to another. [relativized = other - this]",  Vec3dScriptObject.class, new NamedParam("other", Vec3dScriptObject.class));
        f.add_Desc("lerp",         args -> new Vec3dScriptObject((Vec3d) withOther(args, v -> vec.lerp(v, args[2].asDouble()))), "Returns a vector representing this linearly interpolated towards another vector by the value supplied. This value should be a float between 0 and 1.", Vec3dScriptObject.class, new NamedParam("other", Vec3dScriptObject.class), new NamedParam("by", Number.class));

        f.add_NoParams_Desc("normalize", args -> new Vec3dScriptObject(vec.normalize()), "Returns a vector with direction equivalent to this, but with its magnitude reduced to 1.", Vec3dScriptObject.class);
        f.add_NoParams_Desc("negate", args -> new Vec3dScriptObject(vec.negate()), "Negates this vector; in other words, multiplies this vector by -1.", Vec3dScriptObject.class);

        f.add_NoParams_Desc("to_string", args -> vec.toString(), "Returns the string representation of this vector ['(x, y, z)']", String.class);
    }
}
