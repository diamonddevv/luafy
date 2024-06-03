package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.api.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.api.obj.entity.display.TextDisplayEntityScriptObject;
import dev.diamond.luafy.script.api.obj.util.ByteBufScriptObject;
import dev.diamond.luafy.util.HexId;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class LuafyApi extends AbstractScriptApi {
    public LuafyApi(AbstractScript<?> script) {
        super(script, "luafy");
    }

    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        HashMap<String, AdaptableFunction> f = new HashMap<>();

        f.put("hexid_as_string", args -> HexId.fromString(args[0].asString()));
        f.put("hexid_from_string", args -> HexId.fromString(args[0].asString()));

        f.put("get_system_rtc", args -> System.currentTimeMillis());

        f.put("get_resource", args -> new ByteBufScriptObject(ScriptManager.STATIC_RESOURCES.get(args[0].asString())));
        f.put("get_resource_ids", args -> {
            AdaptableFunction predicateFunction = args[0].asFunction();
            boolean includeData = args.length > 1 && args[1].asBoolean();
            Collection<String> ids = new ArrayList<>();

            ScriptManager.STATIC_RESOURCES.forEach((id, buf) -> {
                boolean test = args[0].adapt(
                        predicateFunction.call(
                                args[0].adapt(id),
                                includeData ? args[0].adapt(new ByteBufScriptObject(buf)) : null
                        )
                ).asBoolean();
                if (test) ids.add(id);
            });

            return ids;
        });

        f.put("test", args -> {
            Vec3d pos = script.source.getPosition();
            World world = script.source.getWorld();

            var e = EntityType.TEXT_DISPLAY.create(world);
            e.setPosition(pos);
            world.spawnEntity(e);

            return new TextDisplayEntityScriptObject(e);
        });

        return f;
    }
}
