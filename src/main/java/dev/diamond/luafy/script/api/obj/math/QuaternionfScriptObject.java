package dev.diamond.luafy.script.api.obj.math;

import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import org.joml.Quaternionf;

public class QuaternionfScriptObject extends AbstractTypedScriptObject {

    private final Quaternionf quaternion;

    public QuaternionfScriptObject(Quaternionf quaternion) {
        this.quaternion = quaternion;
    }

    public Quaternionf get() {
        return quaternion;
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {

        f.add_NoParams("get_x", args -> quaternion.x, Number.class);
        f.add_NoParams("get_y", args -> quaternion.y, Number.class);
        f.add_NoParams("get_z", args -> quaternion.z, Number.class);
        f.add_NoParams("get_w", args -> quaternion.w, Number.class);

        f.add_Void("set_x", args -> quaternion.x = args[0].asFloat(), new NamedParam("value", Number.class));
        f.add_Void("set_y", args -> quaternion.x = args[0].asFloat(), new NamedParam("value", Number.class));
        f.add_Void("set_z", args -> quaternion.x = args[0].asFloat(), new NamedParam("value", Number.class));
        f.add_Void("set_w", args -> quaternion.x = args[0].asFloat(), new NamedParam("value", Number.class));
    }
}
