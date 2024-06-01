package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.api.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.api.obj.math.Matrix4fScriptObject;
import dev.diamond.luafy.script.api.obj.math.QuaternionfScriptObject;
import dev.diamond.luafy.script.api.obj.math.Vec3dScriptObject;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.HashMap;

public class ObjectsApi extends AbstractScriptApi {
    public ObjectsApi(AbstractScript<?> script) {
        super(script, "objects");
    }

    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        HashMap<String, AdaptableFunction> f = new HashMap<>();

        f.put("vec3d", args -> new Vec3dScriptObject(new Vec3d(args[0].asDouble(), args[1].asDouble(), args[2].asDouble())));
        f.put("vec3d_u", args -> new Vec3dScriptObject(new Vec3d(args[0].asDouble(), args[0].asDouble(), args[0].asDouble())));

        f.put("quaternion", args -> new QuaternionfScriptObject(new Quaternionf(args[0].asDouble(), args[1].asDouble(), args[2].asDouble(), args[3].asDouble())));
        f.put("quaternion_u", args -> new QuaternionfScriptObject(new Quaternionf(args[0].asDouble(), args[0].asDouble(), args[0].asDouble(), args[0].asDouble())));
        f.put("quaternion_vec", args -> {
           Vec3d v = args[0].asScriptObjectAssertive(Vec3dScriptObject.class).get();
           double a = args[1].asDouble();

           return new QuaternionfScriptObject(new Quaternionf(v.x, v.y, v.z, a));
        });

        f.put("matrix4", args -> {
            float[] g = new float[16];

            for (int i = 0; i < 16; i++) {
                g[i] = args[i].asFloat();
            }

            return new Matrix4fScriptObject(new Matrix4f(
                    g[0 ], g[1 ], g[2 ], g[3 ],
                    g[4 ], g[5 ], g[6 ], g[7 ],
                    g[8 ], g[9 ], g[10], g[11],
                    g[12], g[13], g[14], g[15]
            ));
        });
        f.put("matrix4_u", args -> {
            float g = args[0].asFloat();

            return new Matrix4fScriptObject(new Matrix4f(
                    g, g, g, g,
                    g, g, g, g,
                    g, g, g, g,
                    g, g, g, g
            ));
        });
        f.put("matrix_quats", args -> {

            Quaternionf a = args[0].asScriptObjectAssertive(QuaternionfScriptObject.class).get();
            Quaternionf b = args[1].asScriptObjectAssertive(QuaternionfScriptObject.class).get();
            Quaternionf c = args[2].asScriptObjectAssertive(QuaternionfScriptObject.class).get();
            Quaternionf d = args[3].asScriptObjectAssertive(QuaternionfScriptObject.class).get();

            return new Matrix4fScriptObject(new Matrix4f(
                    a.x, a.y, a.z, a.w,
                    b.x, b.y, b.z, b.w,
                    c.x, c.y, c.z, c.w,
                    d.x, d.y, d.z, d.w
            ));
        });


        return f;
    }
}
