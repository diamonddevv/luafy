package dev.diamond.luafy.script.api;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.api.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class WebApi extends AbstractScriptApi {
    public WebApi(AbstractScript<?> script) {
        super(script, "web");
    }

    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        HashMap<String, AdaptableFunction> f = new HashMap<>();

        f.put("get", args -> {
            try {
                URL url = new URL(args[0].asString());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("accept", "application/json");

                InputStream response = con.getInputStream();
                byte[] b = response.readAllBytes();

                con.disconnect();
                response.close();
                return new String(b, StandardCharsets.UTF_8);

            } catch (Exception e) {
                Luafy.LOGGER.warn("Could not make web request: " + e);
            }
            return null;
        });

        return f;
    }
}
