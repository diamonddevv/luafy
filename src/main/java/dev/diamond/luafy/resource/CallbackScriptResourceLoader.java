package dev.diamond.luafy.resource;

import com.google.gson.Gson;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.old.LuafyLua;
import dev.diamond.luafy.script.old.ScriptCallbacks;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class CallbackScriptResourceLoader implements SimpleSynchronousResourceReloadListener {
    public static final String FILENAME = "_callback_lua_scripts.json";

    public static final Gson GSON = new Gson();

    @Override
    public Identifier getFabricId() {
        return Luafy.id("callback_script_resource");
    }

    @Override
    public void reload(ResourceManager manager) {
        // Clear Cache Phase
        LuafyLua.CALLBACK_SCRIPTS.clear();

        // Read Phase - path is root
        for (Identifier id : manager.findResources(LuaScriptResourceLoader.PATH, path -> {
            String[] splits = path.getPath().split("/");
            return Objects.equals(splits[splits.length - 1], FILENAME);
        }).keySet()) {
            if (manager.getResource(id).isPresent()) {

                try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                    // Consume stream
                    byte[] bytes = stream.readAllBytes();
                    String s = new String(bytes, StandardCharsets.UTF_8);

                    ScriptCallbacks.CallbackScriptBean bean = GSON.fromJson(s, ScriptCallbacks.CallbackScriptBean.class);

                    LuafyLua.CALLBACK_SCRIPTS.add(bean);
                } catch (Exception e) {
                    Luafy.LOGGER.error("Error occurred while loading Lua Script " + id.toString(), e);
                }
            }
        }
    }
}
