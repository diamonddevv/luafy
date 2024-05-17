package dev.diamond.luafy.resource;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.old.LuafyLua;
import dev.diamond.luafy.script.SandboxStrategies;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;

public class SandboxStrategyResourceLoader implements SimpleSynchronousResourceReloadListener {

    public static final String PATH = "sandboxes";
    public static final String EXTENSION = ".json";

    public static final Gson gson = new Gson();

    @Override
    public Identifier getFabricId() {
        return Luafy.id("sandbox_strategies");
    }

    @Override
    public void reload(ResourceManager manager) {
        // Clear Cache Phase
        ScriptManager.SANDBOX_STRATEGIES.clear();

        // Read Phase
        for (Identifier id : manager.findResources(PATH, path -> path.getPath().endsWith(EXTENSION)).keySet()) {
            if (manager.getResource(id).isPresent()) {

                try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                    // Consume stream

                    SandboxStrategies.Strategy strategy =
                            gson.fromJson(new JsonReader(new InputStreamReader(stream)), SandboxStrategies.Strategy.class);


                    int pfLen = (PATH + "/").length();
                    String fixedPath = id.getPath().substring(pfLen);
                    fixedPath = fixedPath.substring(0, fixedPath.length() - EXTENSION.length());
                    String newId = id.getNamespace() + ":" + fixedPath;

                    ScriptManager.SANDBOX_STRATEGIES.put(newId, strategy);
                } catch (Exception e) {
                    Luafy.LOGGER.error("Error occurred while loading Sandbox Strategy: " + id.toString(), e);
                }
            }
        }
    }
}
