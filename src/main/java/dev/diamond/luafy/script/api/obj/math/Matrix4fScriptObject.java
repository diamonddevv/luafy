package dev.diamond.luafy.script.api.obj.math;

import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;

public class Matrix4fScriptObject extends AbstractTypedScriptObject {

    private final Matrix4f mat;

    public Matrix4fScriptObject(Matrix4f mat) {
        this.mat = mat;
    }

    public Matrix4f get() {
        return mat;
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {

        f.add_Void_Desc("set_translation", args -> {
            Vector3f v = args[0].asScriptObjectAssertive(Vec3dScriptObject.class).get().toVector3f();
            mat.setTranslation(v.x, v.y, v.z);
            return null;
        }, "Sets the translation of this matrix.", new NamedParam("translation", Vec3dScriptObject.class));

        f.add_Void_Desc("scale", args -> {
            Vector3f v = args[0].asScriptObjectAssertive(Vec3dScriptObject.class).get().toVector3f();
            mat.scale(v.x, v.y, v.z);
            return null;
        }, "Scales this matrix.", new NamedParam("scale", Vec3dScriptObject.class));

        f.add_Void_Desc("rotate", args -> {
            Quaternionf quat = args[0].asScriptObjectAssertive(QuaternionfScriptObject.class).get();
            mat.rotate(quat);
            return null;
        }, "Rotates this matrix by the given quaternion.", new NamedParam("quaternion", QuaternionfScriptObject.class));

        f.add_NoParams_Desc("to_string", args ->
                String.format(
                        "[%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s]",
                        mat.m00(), mat.m01(), mat.m02(), mat.m03(),
                        mat.m10(), mat.m11(), mat.m12(), mat.m13(),
                        mat.m20(), mat.m21(), mat.m22(), mat.m23(),
                        mat.m30(), mat.m31(), mat.m32(), mat.m33()
                ), "Returns this matrix as a String in the format [a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p].", String.class);

        f.add_NoParams_Desc("to_list", args -> Arrays.stream(new Float[] {
                mat.m00(), mat.m01(), mat.m02(), mat.m03(),
                mat.m10(), mat.m11(), mat.m12(), mat.m13(),
                mat.m20(), mat.m21(), mat.m22(), mat.m23(),
                mat.m30(), mat.m31(), mat.m32(), mat.m33()
        }).toList(), "Returns this matrix as a list of elements.", List.class);
    }
}
