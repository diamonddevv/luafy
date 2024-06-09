package dev.diamond.luafy.script.api;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebApi extends AbstractTypedScriptApi {
    public WebApi(AbstractScript<?> script) {
        super(script, "web");
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {
        f.add_Desc("get", args -> {
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
        }, "Makes a GET request to the given URL. Returns web server's JSON response as a string.", String.class, new NamedParam("url", String.class));
    }

    @Override
    public String getDescription() {
        return "Provides functions relating to various web-request related functions. Note that improper usage of this could expose security vulnerabilities.";
    }
}
