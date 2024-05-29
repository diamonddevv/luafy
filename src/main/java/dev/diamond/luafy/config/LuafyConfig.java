package dev.diamond.luafy.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.registry.sandbox.Strategy;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class LuafyConfig {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Strategy DEFUALT_STRATEGY = Strategy.of(true,
            "luafy:lua_io", "luafy:lua_os", "luafy:lua_luajava");



    public static Config GLOBAL_CONFIG;

    public static class Config {

        @SerializedName("sandbox_strategy")
        public Strategy strategy = DEFUALT_STRATEGY;


        @SerializedName("script_threading_allowed")
        public boolean scriptThreading = false;

        @SerializedName("parsed_command_modification-command_api")
        public boolean allowParsedCommandEditing = true;

        public Strategy getStrategy() {
            return strategy;
        }
    }

    private static File getFile() {
        Path path = FabricLoader.getInstance().getConfigDir();
        return new File(path.toString() + File.separator + "luafy.json");
    }
    public static void initializeConfig() {
        File file = getFile();

        if (file.exists() && file.canRead()) {
            try (FileInputStream stream = new FileInputStream(file);) {
                GLOBAL_CONFIG = GSON.fromJson(new JsonReader(new InputStreamReader(stream)), Config.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            GLOBAL_CONFIG = new Config();
            Luafy.LOGGER.info("Created new Config.");
            writeConfig();
            initializeConfig();
        }
    }
    public static void writeConfig() {
        File file = getFile();

        try {
            if (!file.exists()) {
                boolean ignored = file.createNewFile();
            }

            if (file.canWrite()) {
                try (FileOutputStream stream = new FileOutputStream(file)) {

                    String s = GSON.toJson(GLOBAL_CONFIG);
                    byte[] data = s.getBytes(StandardCharsets.UTF_8);
                    stream.write(data, 0, data.length);

                } catch (IOException e) { throw new RuntimeException(e); }
            }
        } catch (IOException e) { throw new RuntimeException(e); }

    }

}
