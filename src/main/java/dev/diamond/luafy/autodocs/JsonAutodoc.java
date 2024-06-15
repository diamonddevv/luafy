package dev.diamond.luafy.autodocs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.TypedFunctions;
import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import dev.diamond.luafy.script.registry.callback.ScriptCallbackEvent;
import dev.diamond.luafy.script.registry.lang.ScriptLanguage;
import dev.diamond.luafy.util.DescriptionProvider;

import java.nio.charset.StandardCharsets;

public class JsonAutodoc implements Autodoc<JsonObject, JsonArray> {

    private final String filename;

    public JsonAutodoc(String filename) {
        this.filename = filename;
    }

    @Override
    public JsonObject getBlankFormat() {
        return new JsonObject();
    }

    @Override
    public JsonArray getEmptyList() {
        return new JsonArray();
    }

    @Override
    public byte[] completedToBytes(JsonObject format) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        return gson.toJson(format).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void addList(JsonObject head, JsonArray list, String snakeCaseKey, String titleCaseKey) {
        head.add(snakeCaseKey, list);
    }

    @Override
    public void addTypedApi(AbstractTypedScriptApi typedApi, JsonArray list) {
        JsonObject obj = new JsonObject();

        obj.addProperty("name", typedApi.name);
        addDesc(typedApi, obj);

        JsonArray funcs = new JsonArray();
        typedApi.getTypedFunctionList().getHash().forEach((k, v) -> funcs.add(functionToJson(k , v)));

        obj.add("functions", funcs);

        list.add(obj);
    }

    @Override
    public void addTypedObject(AbstractTypedScriptObject<?> obj, JsonArray list) {
        JsonObject o = new JsonObject();

        o.addProperty("name", obj.getClass().getSimpleName());
        addDesc(obj, o);

        JsonArray funcs = new JsonArray();
        obj.getTypedFunctionList().getHash().forEach((k, v) -> funcs.add(functionToJson(k , v)));

        o.add("functions", funcs);

        list.add(o);
    }

    @Override
    public void addEvent(ScriptCallbackEvent event, JsonArray list) {
        JsonObject object = new JsonObject();
        object.addProperty("name", event.toString());
        addDesc(event, object);

        if (event.getContextParams() != null) {
            JsonArray contextParams = new JsonArray();
            for (int i = 0; i < event.getContextParams().length; i++) {
                contextParams.add(namedParamToJson(event.getContextParams()[i]));
            }
            object.add("context_parameters", contextParams);
        }


        list.add(object);
    }

    @Override
    public void addLang(ScriptLanguage<?> lang, JsonArray list) {
        JsonObject object = new JsonObject();

        object.addProperty("id", Luafy.Registries.SCRIPT_LANGUAGES.getId(lang).toString());
        addDesc(lang, object);

        JsonArray exts = new JsonArray();
        for (var ext : lang.getFileExtensions()) exts.add(ext);
        object.add("file_extensions", exts);

        object.addProperty("implementer", lang.getImplementerCredits());
        object.addProperty("language_documentation", lang.getLanguageDocumentationUrl());

        list.add(object);
    }

    public static JsonObject functionToJson(String funcName, TypedFunctions.TypeData data) {
        JsonObject f = new JsonObject();

        JsonArray params = new JsonArray();
        JsonArray opParams = new JsonArray();

        for (var p : data.params()) params.add(namedParamToJson(p));
        for (var p : data.optionalParams()) opParams.add(namedParamToJson(p));

        f.addProperty("name", funcName);
        f.addProperty("description", data.description().orElse(""));
        f.add("parameters", params);
        f.add("optional_parameters", opParams);
        f.addProperty("return_type", data.returnType().map(Class::getSimpleName).orElse(""));

        return f;
    }

    public static JsonObject namedParamToJson(NamedParam p) {
        JsonObject o = new JsonObject();
        o.addProperty("name", p.name);
        o.addProperty("type", p.clazz.getSimpleName());
        return o;
    }

    public static void addDesc(DescriptionProvider p, JsonObject o) {
        o.addProperty("description", p.getDescription());
    }


    @Override
    public String getFilename() {
        return filename;
    }
}
