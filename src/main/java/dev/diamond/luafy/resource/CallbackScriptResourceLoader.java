package dev.diamond.luafy.resource;

import com.google.gson.Gson;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.old.LuafyLua;
import dev.diamond.luafy.script.ScriptCallbacks;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CallbackScriptResourceLoader implements SimpleSynchronousResourceReloadListener {
    public static final String PATH = "callbacks";

    public static final Gson GSON = new Gson();

    @Override
    public Identifier getFabricId() {
        return Luafy.id("callback_script_resource");
    }

    @Override
    public void reload(ResourceManager manager) {
        // Clear Cache Phase
        ScriptManager.CALLBACKS.clear();

        // Read Phase - path is root
        for (Identifier id : manager.findResources(LuaScriptResourceLoader.PATH, path -> path.getPath().endsWith(".json")).keySet()) {
            if (manager.getResource(id).isPresent()) {

                try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                    // Consume stream
                    byte[] bytes = stream.readAllBytes();
                    String s = new String(bytes, StandardCharsets.UTF_8);

                    ScriptCallbacks.CallbackScriptBean bean = GSON.fromJson(s, ScriptCallbacks.CallbackScriptBean.class);

                    ScriptManager.CALLBACKS.add(bean);
                } catch (Exception e) {
                    Luafy.LOGGER.error("Error occurred while loading Callbacks " + id.toString(), e);
                }
            }
        }
    }
}
