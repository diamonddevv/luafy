package dev.diamond.luafy.autodocs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.TypedFunctions;
import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import dev.diamond.luafy.script.registry.sandbox.SandboxableLuafyModApi;
import net.minecraft.registry.Registry;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;

public class JsonDocsCreator implements DocsCreator {


    private final String filename;

    public JsonDocsCreator(String filename) {
        this.filename = filename;
    }

    @Override
    public byte[] getBytesToWriteToFile() {
        JsonObject json = new JsonObject();

        populate(json, "API", ".", Luafy.Registries.API_REGISTRY, e -> {
            if (e instanceof SandboxableLuafyModApi slmi) {
                var api = slmi.getApiProvider().provide(ApiDocSpitterOutter.DUMMY);
                if (api instanceof AbstractTypedScriptApi t) {
                    return Optional.of(t);
                }
            }
            return Optional.empty();
        }, api -> api.name);

        populate(json, "OBJECT", "#", Luafy.Registries.SCRIPT_OBJECTS_REGISTRY, e -> {
            IScriptObject object = e.create(new Object[256]);
            // create with 256 nothings, we dont need to use this ones functions we just need the information
            // and if theres over 2^8 params you have your own problems

            if (object instanceof AbstractTypedScriptObject atso) {
                return Optional.of(atso);
            }
            return Optional.empty();
        }, obj -> obj.getClass().getSimpleName());


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String s = gson.toJson(json);
        return s.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getFilenameToUse() {
        return filename;
    }

    private static <T extends TypedFunctions, R> void populate(
            JsonObject json,
            String type, String delimiter,
            Registry<R> registry,
            ApiDocSpitterOutter.TypedFunctionsProvider<T, R> provider, Function<T, String> nameFunc)
    {
        for (var e : registry) {

            Optional<T> ot = provider.provide(e);

            if (ot.isPresent()) {
                T t = ot.get();
                String name = nameFunc.apply(t);

                t.getUntypedFunctions(); // init

                JsonArray list = new JsonArray();

                for (var kvp : t.getTypedFunctionList().getHash().entrySet()) {
                    JsonObject objJson = new JsonObject();

                    objJson.addProperty("name", kvp.getKey());

                    JsonArray params = new JsonArray();
                    JsonArray optionalParams = new JsonArray();

                    for (var p : kvp.getValue().params()) {
                        JsonObject o = new JsonObject();
                        o.addProperty("name", p.name);
                        o.addProperty("type", p.clazz.getSimpleName());
                        params.add(o);
                    }

                    for (var p : kvp.getValue().optionalParams()) {
                        JsonObject o = new JsonObject();
                        o.addProperty("name", p.name);
                        o.addProperty("type", p.clazz.getSimpleName());
                        optionalParams.add(o);
                    }

                    objJson.add("parameter_types", params);
                    objJson.add("optional_parameter_types", optionalParams);

                    objJson.addProperty("return_type", kvp.getValue().returnType().map(Class::getSimpleName).orElse("void"));

                    list.add(objJson);

                    Luafy.LOGGER.info("[Function Signature Generator]: Added function " + name + delimiter + kvp.getKey());
                }

                json.add(type + "_" + name, list);
            }
        }
    }
}
