package dev.diamond.luafy.script.registry.sandbox;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.api.ApiProvider;
import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import dev.diamond.luafy.script.abstraction.obj.ScriptObjectProvider;
import dev.diamond.luafy.script.registry.lang.ScriptLanguage;
import net.fabricmc.loader.impl.FabricLoaderImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public class ApiDocSpitterOutter {

    public static class DummyScript extends AbstractScript<DummyScriptValue> {

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
    public static class DummyScriptValue extends AbstractBaseValue<Object, DummyScriptValue> {

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

    public static void spitOutDocs() {
        Collection<String> lines = new ArrayList<>();

        for (SandboxableApi<?> sa : Luafy.Registries.API_REGISTRY) {
            if (sa instanceof SandboxableLuafyModApi slmi) {
                var api = slmi.getApiProvider().provide(DUMMY);

                if (api instanceof AbstractTypedScriptApi t) {
                    lines.add("API | " + t.name + " : ");
                    lines.addAll(t.formSignatures(true));
                }
            }
        }

        // do file thing
        try {
            File file = FabricLoaderImpl.INSTANCE.getGameDir().getFileSystem().getPath(
                    FabricLoaderImpl.INSTANCE.getGameDir().toString(),
                    "functions.txt"
            ).toFile();
            boolean ignored = file.createNewFile();
            try (OutputStream stream = new FileOutputStream(file)) {
                StringBuilder b = new StringBuilder();

                for (String line : lines) {
                    b.append(line).append("\n");
                }

                stream.write(b.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            Luafy.LOGGER.warn("AA: " +  e);
        }
    }
}
