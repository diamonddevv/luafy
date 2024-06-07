package dev.diamond.luafy.script.api.obj.math;

import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.HashMap;

public class Matrix4fScriptObject implements IScriptObject {

    private final Matrix4f mat;

    public Matrix4fScriptObject(Matrix4f mat) {
        this.mat = mat;
    }

    @Override
    public void addFunctions(HashMap<String, AdaptableFunction> set) {
        set.put("set_translation", args -> {
           Vector3f v = args[0].asScriptObjectAssertive(Vec3dScriptObject.class).get().toVector3f();
           mat.setTranslation(v.x, v.y, v.z);
           return null;
        });

        set.put("scale", args -> {
            Vector3f v = args[0].asScriptObjectAssertive(Vec3dScriptObject.class).get().toVector3f();
            mat.scale(v.x, v.y, v.z);
            return null;
        });

        set.put("rotate", args -> {
            Quaternionf quat = args[0].asScriptObjectAssertive(QuaternionfScriptObject.class).get();

            mat.rotate(quat);

            return null;
        });




        set.put("to_string", args ->
            String.format(
                    "[%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s]",
                    mat.m00(), mat.m01(), mat.m02(), mat.m03(),
                    mat.m10(), mat.m11(), mat.m12(), mat.m13(),
                    mat.m20(), mat.m21(), mat.m22(), mat.m23(),
                    mat.m30(), mat.m31(), mat.m32(), mat.m33()
            )
        );
        set.put("to_list", args -> Arrays.stream(new Float[] {
                mat.m00(), mat.m01(), mat.m02(), mat.m03(),
                mat.m10(), mat.m11(), mat.m12(), mat.m13(),
                mat.m20(), mat.m21(), mat.m22(), mat.m23(),
                mat.m30(), mat.m31(), mat.m32(), mat.m33()
        }).toList());
    }

    public Matrix4f get() {
        return mat;
    }
}
