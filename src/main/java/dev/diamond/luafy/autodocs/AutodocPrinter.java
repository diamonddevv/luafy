package dev.diamond.luafy.autodocs;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.api.ApiProvider;
import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import dev.diamond.luafy.script.abstraction.obj.ScriptObjectProvider;
import dev.diamond.luafy.script.registry.lang.ScriptLanguage;
import dev.diamond.luafy.script.registry.sandbox.SandboxableLuafyModApi;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Predicate;

public class AutodocPrinter {


    private static class DummyScript extends AbstractScript<DummyScriptValue> {

        @Override
        public DummyScriptValue executeScript() {
            return null;
        }

        @Override
        public void addApi(ApiProvider api) {
        }

        @Override
        public DummyScriptValue getNullBaseValue() {
            return null;
        }

        @Override
        public ScriptLanguage<?> getLanguage() {
            return null;
        }
    }
    private static class DummyScriptValue extends AbstractBaseValue<Object, DummyScriptValue> {

        public DummyScriptValue(Object o) {
            super(o);
        }

        @Override
        public String asString() {
            return null;
        }

        @Override
        public Object getLangNull() {
            return null;
        }

        @Override
        public AdaptableFunction asFunction() {
            return null;
        }

        @Override
        public HashMap<DummyScriptValue, DummyScriptValue> asMap() {
            return null;
        }

        @Override
        public Collection<DummyScriptValue> asCollection() {
            return null;
        }

        @Override
        public Object asJavaObject() {
            return null;
        }

        @Override
        public boolean isString() {
            return false;
        }

        @Override
        public boolean isByte() {
            return false;
        }

        @Override
        public boolean isInt() {
            return false;
        }

        @Override
        public boolean isLong() {
            return false;
        }

        @Override
        public boolean isFloat() {
            return false;
        }

        @Override
        public boolean isDouble() {
            return false;
        }

        @Override
        public boolean isBool() {
            return false;
        }

        @Override
        public boolean isMap() {
            return false;
        }

        @Override
        public boolean isCollection() {
            return false;
        }

        @Override
        public boolean isFunction() {
            return false;
        }

        @Override
        public DummyScriptValue adaptAbstract(Object obj) {
            return null;
        }

        @Override
        public DummyScriptValue addObject(ScriptObjectProvider obj) {
            return null;
        }

        @Override
        public Optional<IScriptObject> asScriptObjectIfPresent() {
            return Optional.empty();
        }
    }

    public static final DummyScript DUMMY = new DummyScript();


    public static Predicate<Identifier> getNamespacePredicate(String namespace) {
        return id -> id.getNamespace().matches(namespace);
    }

    public static void printDocs() {
        printDocs(id -> true);
    }
    public static void printDocs(String namespace) {
        printDocs(getNamespacePredicate(namespace));
    }
    public static void printDocs(Predicate<Identifier> idPredicate) {
        Luafy.LOGGER.info("Generating docs files..");

        for (Autodoc<?, ?> autodoc : Luafy.Collections.AUTODOCS) {
            // do file write
            try {
                float percentage = writeDoc(autodoc, idPredicate);

                Luafy.LOGGER.info("Written " + autodoc.getFilename() + " autodoc. (Documentation " + percentage * 100 + "% complete)");
            } catch (IOException e) {
                Luafy.LOGGER.warn("Couldn't write docs file: " + e);
            }
        }
    }

    public static void addAll() {
        Luafy.Collections.AUTODOCS.add(new JsonAutodoc("luafy_autodocs.json"));
        Luafy.Collections.AUTODOCS.add(new MarkdownAutodoc("luafy_markdown_autodoc.md"));
    }

    private static <F, A> float writeDoc(Autodoc<F, A> autodoc, Predicate<Identifier> idPredicate) throws IOException {

        int things = 0;
        int written = 0;

        F obj = autodoc.getBlankFormat();

        A apis = autodoc.getEmptyList();
        A objs = autodoc.getEmptyList();
        A events = autodoc.getEmptyList();
        A langs = autodoc.getEmptyList();

        autodoc.preApis(apis);
        for (var api : Luafy.Registries.APIS) {
            if (!idPredicate.test(Luafy.Registries.APIS.getId(api))) continue;
            things += 1;

            if (api instanceof SandboxableLuafyModApi slmapi) {
                var asapi = slmapi.getApiProvider().provide(DUMMY);
                if (asapi instanceof AbstractTypedScriptApi tapi) {
                    tapi.getUntypedFunctions();
                    autodoc.addTypedApi(tapi, apis);
                    written += 1;
                }
            }
        }

        autodoc.preObjects(objs);
        for (var o : Luafy.Registries.SCRIPT_OBJECTS) {
            if (!idPredicate.test(Luafy.Registries.SCRIPT_OBJECTS.getId(o))) continue;

            things += 1;
            IScriptObject<?> object = o.create(new Object[256]);
            // create with 256 nothings, we dont need to use this ones functions we just need the information
            // and if theres over 2^8 params you have your own problems

            if (object instanceof AbstractTypedScriptObject<?> tso) {

                tso.getUntypedFunctions();
                autodoc.addTypedObject(tso, objs);
                written += 1;
            }
        }

        autodoc.preEvents(events);
        for (var o : Luafy.Registries.EVENT_CALLBACKS) {
            if (!idPredicate.test(Luafy.Registries.EVENT_CALLBACKS.getId(o))) continue;
            autodoc.addEvent(o, events);
            things += 1;
            written += 1;
        }

        autodoc.preLangs(langs);
        for (var o : Luafy.Registries.SCRIPT_LANGUAGES) {
            if (!idPredicate.test(Luafy.Registries.SCRIPT_LANGUAGES.getId(o))) continue;
            autodoc.addLang(o, langs);

            things += 1;
            written += 1;
        }

        autodoc.addList(obj, apis, "apis", "APIs");
        autodoc.addList(obj, objs, "script_objects", "Script Objects");
        autodoc.addList(obj, events, "callback_events", "Callback Events");
        autodoc.addList(obj, langs, "script_languages", "Script Languages");

        File dir = FabricLoaderImpl.INSTANCE.getGameDir().getFileSystem().getPath(
                FabricLoaderImpl.INSTANCE.getGameDir().toString(),
                "autodocs"
        ).toFile();

        dir.mkdirs();

        File file = Paths.get(dir.toString(), autodoc.getFilename()).toFile();

        file.createNewFile();

        try (OutputStream stream = new FileOutputStream(file)) {
            stream.write(autodoc.completedToBytes(obj));
        }

        return (float) written / things; // percentage of documentation written
    }


}
