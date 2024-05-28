package dev.diamond.luafy.script;

import com.google.gson.annotations.SerializedName;
import dev.diamond.luafy.config.LuafyConfig;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.api.*;
import dev.diamond.luafy.script.lua.LuaScript;
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
                    "lua_base", "lua_package", "lua_bit32", "lua_table", "lua_string", "lua_coroutine", "lua_math", "lua_io", "lua_os", "lua_luajava",
                    "luafy", "command", "server", "context", "storage", "script", "threads"
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

    public static void applyAbstractSandbox(AbstractScript<?> script) {
        List<String> m = parseStrategy(LuafyConfig.GLOBAL_CONFIG.getStrategy());

        for (var mod : m) {
            switch (mod) {

                // lua
                case "lua_base" ->      operateAsLuaOrPass(script, s -> s.scriptGlobals.load(new JseBaseLib()));
                case "lua_package" ->   operateAsLuaOrPass(script, s -> s.scriptGlobals.load(new PackageLib()));
                case "lua_bit32" ->     operateAsLuaOrPass(script, s -> s.scriptGlobals.load(new Bit32Lib()));
                case "lua_table" ->     operateAsLuaOrPass(script, s -> s.scriptGlobals.load(new TableLib()));
                case "lua_string" ->    operateAsLuaOrPass(script, s -> s.scriptGlobals.load(new JseStringLib()));
                case "lua_coroutine" -> operateAsLuaOrPass(script, s -> s.scriptGlobals.load(new CoroutineLib()));
                case "lua_math" ->      operateAsLuaOrPass(script, s -> s.scriptGlobals.load(new JseMathLib()));
                case "lua_io" ->        operateAsLuaOrPass(script, s -> s.scriptGlobals.load(new JseIoLib()));
                case "lua_os" ->        operateAsLuaOrPass(script, s -> s.scriptGlobals.load(new JseOsLib()));
                case "lua_luajava" ->   operateAsLuaOrPass(script, s -> s.scriptGlobals.load(new LuajavaLib()));

                // luafy
                case "luafy" ->         script.addApi(LuafyApi::new);
                case "command" ->       script.addApi(CommandApi::new);
                case "server" ->        script.addApi(ServerApi::new);
                case "context" ->       script.addApi(ContextApi::new);
                case "storage" ->       script.addApi(StorageApi::new);
                case "script" ->        script.addApi(ScriptApi::new);
                case "threads" ->       script.addApi(ThreadsApi::new);

            }
        }

        operateAsLuaOrPass(script, s -> {
            LoadState.install(s.scriptGlobals);
            LuaC.install(s.scriptGlobals);
        });
    }

    public static void operateAsLuaOrPass(AbstractScript<?> script, ScriptProvider<LuaScript> provider) {
        if (script instanceof LuaScript l) {
            provider.provide(l);
        }
    }



    public static class Strategy {
        @SerializedName("blacklist")
        public boolean blacklist = false;

        @SerializedName("modules")
        public List<String> modules;
    }

    @FunctionalInterface
    public interface ScriptProvider<T extends AbstractScript<?>> {
        void provide(T script);
    }
}
