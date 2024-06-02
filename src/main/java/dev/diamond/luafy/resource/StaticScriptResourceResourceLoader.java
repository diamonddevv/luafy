package dev.diamond.luafy.resource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.ScriptManager;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;

public class StaticScriptResourceResourceLoader implements SimpleSynchronousResourceReloadListener {

    public static final String PATH = "luafy/resources";
    public static final String EXTENSION = ".json";
    public static final Gson GSON = new Gson();

    @Override
    public Identifier getFabricId() {
        return Luafy.id("static_script_resource_resource_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        // Clear Cache Phase
        ScriptManager.STATIC_RESOURCES.clear();

        // Read Phase
        for (Identifier id : manager.findResources(PATH, id -> id.toString().endsWith(EXTENSION)).keySet()) {
            if (manager.getResource(id).isPresent()) {
                try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                    // Consume
                    JsonObject json = GSON.fromJson(new JsonReader(new InputStreamReader(stream)), JsonObject.class);

                    String[] s = id.toString().split(":");
                    s[1] = s[1].substring(0, s[1].length() - EXTENSION.length());
                    s[1] = s[1].substring(PATH.length() + 1);
                    String strippedId = s[0] + ":" + s[1];


                    ScriptManager.STATIC_RESOURCES.put(strippedId, json);
                } catch (Exception e) {
                    Luafy.LOGGER.error("Error occurred while loading static resource " + id.toString(), e);
                }
            }
        }
    }
}
