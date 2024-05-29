package dev.diamond.luafy.resource;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.lua.LuaScript;
import dev.diamond.luafy.script.registry.lang.ScriptLanguage;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class ScriptResourceLoader implements SimpleSynchronousResourceReloadListener {

    public static final String PATH = "luafy/scripts";

    @Override
    public Identifier getFabricId() {
        return Luafy.id("script_resources");
    }

    @Override
    public void reload(ResourceManager manager) {
        // Clear Cache Phase
        ScriptManager.SCRIPTS.clear();

        // Read Phase
        for (Identifier id : manager.findResources(PATH, path -> true).keySet()) {
            if (manager.getResource(id).isPresent()) {

                String ext = "";
                try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                    // Consume stream
                    byte[] bytes = stream.readAllBytes();
                    String s = new String(bytes, StandardCharsets.UTF_8);

                    // get script
                    ext = id.getPath().split("\\.")[1];
                    var script = loadScript(ext, s);

                    if (script != null) {
                        int pfLen = (PATH + "/").length();
                        String fixedPath = id.getPath().substring(pfLen);
                        fixedPath = fixedPath.substring(0, fixedPath.length() - ext.length() - 1);
                        String newId = id.getNamespace() + ":" + fixedPath;
                        script.name = newId;
                        ScriptManager.SCRIPTS.put(newId, script);
                    } else {
                        Luafy.LOGGER.warn("There was no registered script language using the file extension {}!", ext);
                    }
                } catch (Exception e) {
                    Luafy.LOGGER.error("Error occurred while loading '" + ext + "' Script " + id.toString(), e);
                }
            }
        }
    }

    public static AbstractScript<?> loadScript(String extension, String scriptContent) {
        for (ScriptLanguage<?> s : Luafy.SCRIPT_LANG_REGISTRY) {
            if (Arrays.asList(s.getFileExtensions()).contains(extension)) {
                return s.readScript(scriptContent);
            }
        }
        return null;
    }
}
