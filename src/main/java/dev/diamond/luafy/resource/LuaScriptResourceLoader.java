package dev.diamond.luafy.resource;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.lua.LuaScript;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LuaScriptResourceLoader implements SimpleSynchronousResourceReloadListener {

    public static final String PATH = "lua_scripts";
    public static final String EXTENSION = ".lua";

    @Override
    public Identifier getFabricId() {
        return Luafy.id("script_resources");
    }

    @Override
    public void reload(ResourceManager manager) {
        // Clear Cache Phase
        ScriptManager.SCRIPTS.clear();

        // Read Phase
        for (Identifier id : manager.findResources(PATH, path -> path.getPath().endsWith(EXTENSION)).keySet()) {
            if (manager.getResource(id).isPresent()) {

                try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                    // Consume stream
                    byte[] bytes = stream.readAllBytes();
                    String s = new String(bytes, StandardCharsets.UTF_8);

                    int pfLen = (PATH + "/").length();
                    String fixedPath = id.getPath().substring(pfLen);
                    fixedPath = fixedPath.substring(0, fixedPath.length() - EXTENSION.length());
                    String newId = id.getNamespace() + ":" + fixedPath;


                    LuaScript absScript = new LuaScript(s);
                    ScriptManager.SCRIPTS.put(newId, absScript);
                } catch (Exception e) {
                    Luafy.LOGGER.error("Error occurred while loading Lua Script " + id.toString(), e);
                }
            }
        }
    }
}
