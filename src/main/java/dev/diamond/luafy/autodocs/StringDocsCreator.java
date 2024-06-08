package dev.diamond.luafy.autodocs;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.TypedFunctions;
import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import dev.diamond.luafy.script.registry.sandbox.SandboxableLuafyModApi;
import net.minecraft.registry.Registry;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class StringDocsCreator implements DocsCreator {

    private final String filename;

    public StringDocsCreator(String filename) {
        this.filename = filename;
    }

    @Override
    public byte[] getBytesToWriteToFile() {
        List<String> lines = new ArrayList<>();

        populateLines(lines, "API", ".", Luafy.Registries.API_REGISTRY, e -> {
            if (e instanceof SandboxableLuafyModApi slmi) {
                var api = slmi.getApiProvider().provide(ApiDocSpitterOutter.DUMMY);
                if (api instanceof AbstractTypedScriptApi t) {
                    return Optional.of(t);
                }
            }
            return Optional.empty();
        }, api -> api.name);

        populateLines(lines, "OBJECT", "#", Luafy.Registries.SCRIPT_OBJECTS_REGISTRY, e -> {
            IScriptObject object = e.create(new Object[256]);
            // create with 256 nothings, we dont need to use this ones functions we just need the information
            // and if theres over 2^8 params you have your own problems

            if (object instanceof AbstractTypedScriptObject atso) {
                return Optional.of(atso);
            }
            return Optional.empty();
        }, obj -> obj.getClass().getSimpleName());


        StringBuilder b = new StringBuilder();

        for (String line : lines) {
            b.append(line).append("\n");
        }

        return b.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getFilenameToUse() {
        return filename;
    }

    private static <T extends TypedFunctions, R> void populateLines(
            Collection<String> lines,
            String type, String delimiter,
            Registry<R> registry,
            ApiDocSpitterOutter.TypedFunctionsProvider<T, R> provider, Function<T, String> nameFunc)
    {
        for (var e : registry) {

            Optional<T> ot = provider.provide(e);

            if (ot.isPresent()) {
                T t = ot.get();
                String name = nameFunc.apply(t);

                lines.add(type + " | " + name + " : ");
                lines.addAll(t.formStringSignatures(name, delimiter, true));
                lines.add("\n");

            }
        }
    }
}
