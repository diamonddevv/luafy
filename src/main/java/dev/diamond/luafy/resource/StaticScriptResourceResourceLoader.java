package dev.diamond.luafy.resource;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.ScriptManager;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;

public class StaticScriptResourceResourceLoader implements SimpleSynchronousResourceReloadListener {

    public static final String PATH = "luafy/resources";

    @Override
    public Identifier getFabricId() {
        return Luafy.id("static_script_resource_resource_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        // Clear Cache Phase
        ScriptManager.STATIC_RESOURCES.clear();

        // Read Phase
        for (Identifier id : manager.findResources(PATH, id -> true).keySet()) {
            if (manager.getResource(id).isPresent()) {
                try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                    // Consume
                    byte[] bytes = stream.readAllBytes();

                    String[] s = id.toString().split(":");
                    String ext = s[1].split("\\.")[1];
                    s[1] = s[1].substring(0, s[1].length() - (ext.length() + 1));
                    s[1] = s[1].substring(PATH.length() + 1);
                    String strippedId = s[0] + ":" + s[1];


                    ScriptManager.STATIC_RESOURCES.put(strippedId, bytes);
                } catch (Exception e) {
                    Luafy.LOGGER.error("Error occurred while loading static resource " + id.toString(), e);
                }
            }
        }
    }
}
