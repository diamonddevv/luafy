package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.api.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.api.obj.math.Vec3dScriptObject;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;

public class ObjectsApi extends AbstractScriptApi {
    public ObjectsApi(AbstractScript<?> script) {
        super(script, "objects");
    }

    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        HashMap<String, AdaptableFunction> f = new HashMap<>();

        f.put("vec3d", args -> new Vec3dScriptObject(new Vec3d(args[0].asDouble(), args[1].asDouble(), args[2].asDouble())));

        return f;
    }
}
