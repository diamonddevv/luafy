package dev.diamond.luafy.script.api.obj.math;

import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import org.joml.Quaternionf;

public class QuaternionfScriptObject extends AbstractTypedScriptObject<Quaternionf> {

    private final Quaternionf quaternion;

    public QuaternionfScriptObject(Quaternionf quaternion) {
        this.quaternion = quaternion;
    }

    public Quaternionf get() {
        return quaternion;
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {

        f.add_NoParams_Desc("get_x", args -> quaternion.x, "Gets the X component of this quaternion.", Number.class);
        f.add_NoParams_Desc("get_y", args -> quaternion.y, "Gets the Y component of this quaternion.", Number.class);
        f.add_NoParams_Desc("get_z", args -> quaternion.z, "Gets the Z component of this quaternion.", Number.class);
        f.add_NoParams_Desc("get_w", args -> quaternion.w, "Gets the W scalar component of this quaternion", Number.class);

        f.add_Void_Desc("set_x", args -> quaternion.x = args[0].asFloat(), "Sets the X component of this quaternion.", new NamedParam("value", Number.class));
        f.add_Void_Desc("set_y", args -> quaternion.x = args[0].asFloat(), "Sets the Y component of this quaternion.", new NamedParam("value", Number.class));
        f.add_Void_Desc("set_z", args -> quaternion.x = args[0].asFloat(), "Sets the Z component of this quaternion.", new NamedParam("value", Number.class));
        f.add_Void_Desc("set_w", args -> quaternion.x = args[0].asFloat(), "Sets the W scalar component of this quaternion.", new NamedParam("value", Number.class));
    }

    @Override
    public String getName() {
        return "Quaternionf";
    }
}
