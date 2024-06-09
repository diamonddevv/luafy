package dev.diamond.luafy.autodocs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.util.DescriptionProvider;
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

        populateFunctions(json, "apis", ".", Luafy.Registries.API_REGISTRY, e -> {
            if (e instanceof SandboxableLuafyModApi slmi) {
                var api = slmi.getApiProvider().provide(ApiDocSpitterOutter.DUMMY);
                if (api instanceof AbstractTypedScriptApi t) {
                    return Optional.of(t);
                }
            }
            return Optional.empty();
        }, api -> api.name);

        populateFunctions(json, "script_objects", "#", Luafy.Registries.SCRIPT_OBJECTS_REGISTRY, e -> {
            IScriptObject<?> object = e.create(new Object[256]);
            // create with 256 nothings, we dont need to use this ones functions we just need the information
            // and if theres over 2^8 params you have your own problems

            if (object instanceof AbstractTypedScriptObject<?> atso) {
                return Optional.of(atso);
            }
            return Optional.empty();
        }, obj -> obj.getClass().getSimpleName());


        populateEvents(json);
        populateLanguages(json);

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        String s = gson.toJson(json);
        return s.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getFilenameToUse() {
        return filename;
    }

    private static <T extends TypedFunctions & DescriptionProvider, R> void populateFunctions(
            JsonObject json,
            String type, String delimiter,
            Registry<R> registry,
            ApiDocSpitterOutter.TypedFunctionsProvider<T, R> provider,
            Function<T, String> nameFunc)
    {
        JsonObject obj = new JsonObject();

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
                        params.add(namedParamToJson(p));
                    }

                    for (var p : kvp.getValue().optionalParams()) {
                        optionalParams.add(namedParamToJson(p));
                    }

                    objJson.add("parameter_types", params);
                    objJson.add("optional_parameter_types", optionalParams);

                    objJson.addProperty("return_type", kvp.getValue().returnType().map(Class::getSimpleName).orElse("void"));

                    if (kvp.getValue().description().isPresent()) objJson.addProperty("description", kvp.getValue().description().get());

                    list.add(objJson);

                }

                JsonObject obj2 = new JsonObject();
                obj2.addProperty("description", t.getDescription());
                obj2.add("functions", list);

                obj.add(name, obj2);
            }
            json.add(type, obj);
        }
    }

    private static void populateEvents(
            JsonObject json
    ) {
        JsonArray events = new JsonArray();

        for (var e : Luafy.Registries.CALLBACK_REGISTRY) {
            JsonObject event = new JsonObject();
            event.addProperty("id", e.toString());
            if (e.getDescription() != null) event.addProperty("description", e.getDescription());
            if (e.getContextParams() != null){
                JsonArray array = new JsonArray();
                for (var p : e.getContextParams()) {
                    array.add(namedParamToJson(p));
                }
                event.add("context", array);
            }

            events.add(event);
        }

        json.add("callback_events", events);
    }

    private static void populateLanguages(
            JsonObject json
    ) {
        JsonObject langs = new JsonObject();

        for (var e : Luafy.Registries.SCRIPT_LANG_REGISTRY) {
            String id = Luafy.Registries.SCRIPT_LANG_REGISTRY.getId(e).toString();

            JsonObject lang = new JsonObject();

            JsonArray extensions = new JsonArray();
            for (var ext : e.getFileExtensions()) extensions.add(ext);
            lang.add("file_extensions", extensions);

            if (e.getLanguageDocumentationUrl() != null) lang.addProperty("lang_documentation", e.getLanguageDocumentationUrl());
            if (e.getImplementerCredits() != null) lang.addProperty("luafy_implementation_credits", e.getImplementerCredits());
            if (e.getDescription() != null) lang.addProperty("description", e.getDescription());

            langs.add(id, lang);
        }

        json.add("script_languages", langs);
    }


    private static JsonObject namedParamToJson(NamedParam p) {
        JsonObject o = new JsonObject();
        o.addProperty("name", p.name);
        o.addProperty("type", p.clazz.getSimpleName());
        return o;
    }
}
