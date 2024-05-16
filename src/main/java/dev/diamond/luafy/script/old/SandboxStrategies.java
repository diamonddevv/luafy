package dev.diamond.luafy.script.old;

import com.google.gson.annotations.SerializedName;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.api.CommandApi;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.*;

import java.util.ArrayList;
import java.util.List;

public class SandboxStrategies {

    private static final String[] ALL =
            {
                    "base", "package", "bit32", "table", "string", "coroutine", "math", "io", "os", "luajava",
                    "luafy", "command", "server", "context", "storage"
            };

    private static List<String> parseStrategy(Strategy strategy) {
        List<String> m;

        if (strategy.blacklist) {
            m = List.of(ALL);
            List<String> removed = new ArrayList<>();
            for (var e : m) {
                if (!strategy.modules.contains(e)) removed.add(e);
            }
            m = removed;
        } else {
            m = strategy.modules;
        }

        return m;
    }

    public static Globals applySandboxStrategy(Strategy strategy, Old_LuaScript script) {

        List<String> m = parseStrategy(strategy);

        Globals globals = new Globals();
        for (var mod : m) {
            switch (mod) {

                case "base" -> globals.load(new JseBaseLib());
                case "package" -> globals.load(new PackageLib());
                case "bit32" -> globals.load(new Bit32Lib());
                case "table" -> globals.load(new TableLib());
                case "string" -> globals.load(new JseStringLib());
                case "coroutine" -> globals.load(new CoroutineLib());
                case "math" -> globals.load(new JseMathLib());
                case "io" -> globals.load(new JseIoLib());
                case "os" -> globals.load(new JseOsLib());
                case "luajava" -> globals.load(new LuajavaLib());

                case "luafy" -> globals.load(new dev.diamond.luafy.script.old.api.LuafyApi(script));
                case "command" -> globals.load(new dev.diamond.luafy.script.old.api.CommandApi(script));
                case "server" -> globals.load(new dev.diamond.luafy.script.old.api.ServerApi(script));
                case "context" -> globals.load(new dev.diamond.luafy.script.old.api.ContextApi(script));
                case "storage" -> globals.load(new dev.diamond.luafy.script.old.api.StorageApi(script));

            }
        }

        LoadState.install(globals);
        LuaC.install(globals);

        return globals;
    }

    public static void applyAbstractSandbox(AbstractScript<?, ?, ?> script) {
        script.addApi(CommandApi::new);
    }



    public static class Strategy {
        @SerializedName("blacklist")
        public boolean blacklist = false;

        @SerializedName("modules")
        public List<String> modules;
    }
}
