package dev.diamond.luafy.script.registry.sandbox;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.config.LuafyConfig;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.api.*;
import dev.diamond.luafy.script.lua.LuaScript;
import dev.diamond.luafy.script.registry.lang.ScriptLanguages;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.*;

import java.util.ArrayList;
import java.util.List;

public class Apis {

    public static final SandboxableApi<LuaScript> LUA_BASE =        new SandboxableApi<>(ScriptLanguages.LUA, s -> ((LuaScript)s).scriptGlobals.load(new JseBaseLib()));
    public static final SandboxableApi<LuaScript> LUA_PACKAGE =     new SandboxableApi<>(ScriptLanguages.LUA, s -> ((LuaScript)s).scriptGlobals.load(new PackageLib()));
    public static final SandboxableApi<LuaScript> LUA_BIT32 =       new SandboxableApi<>(ScriptLanguages.LUA, s -> ((LuaScript)s).scriptGlobals.load(new Bit32Lib()));
    public static final SandboxableApi<LuaScript> LUA_TABLE =       new SandboxableApi<>(ScriptLanguages.LUA, s -> ((LuaScript)s).scriptGlobals.load(new TableLib()));
    public static final SandboxableApi<LuaScript> LUA_STRING =      new SandboxableApi<>(ScriptLanguages.LUA, s -> ((LuaScript)s).scriptGlobals.load(new JseStringLib()));
    public static final SandboxableApi<LuaScript> LUA_COROUTINE =   new SandboxableApi<>(ScriptLanguages.LUA, s -> ((LuaScript)s).scriptGlobals.load(new CoroutineLib()));
    public static final SandboxableApi<LuaScript> LUA_MATH =        new SandboxableApi<>(ScriptLanguages.LUA, s -> ((LuaScript)s).scriptGlobals.load(new JseMathLib()));
    public static final SandboxableApi<LuaScript> LUA_IO =          new SandboxableApi<>(ScriptLanguages.LUA, s -> ((LuaScript)s).scriptGlobals.load(new JseIoLib()));
    public static final SandboxableApi<LuaScript> LUA_OS =          new SandboxableApi<>(ScriptLanguages.LUA, s -> ((LuaScript)s).scriptGlobals.load(new JseOsLib()));
    public static final SandboxableApi<LuaScript> LUA_LUAJAVA =     new SandboxableApi<>(ScriptLanguages.LUA, s -> ((LuaScript)s).scriptGlobals.load(new LuajavaLib()));

    public static final NonSandboxableApi<LuaScript> LUA_LOADSTATE= new NonSandboxableApi<>(ScriptLanguages.LUA, s -> LoadState.install(((LuaScript)s).scriptGlobals));
    public static final NonSandboxableApi<LuaScript> LUA_C =        new NonSandboxableApi<>(ScriptLanguages.LUA, s -> LuaC.install(((LuaScript)s).scriptGlobals));

    public static final SandboxableApi<?> LUAFY =                   new SandboxableApi<>(null, s -> s.addApi(LuafyApi::new));
    public static final SandboxableApi<?> COMMAND =                 new SandboxableApi<>(null, s -> s.addApi(CommandApi::new));
    public static final SandboxableApi<?> SERVER =                  new SandboxableApi<>(null, s -> s.addApi(ServerApi::new));
    public static final SandboxableApi<?> CONTEXT =                 new SandboxableApi<>(null, s -> s.addApi(ContextApi::new));
    public static final SandboxableApi<?> STORAGE =                 new SandboxableApi<>(null, s -> s.addApi(StorageApi::new));
    public static final SandboxableApi<?> SCRIPT =                  new SandboxableApi<>(null, s -> s.addApi(ScriptApi::new));
    public static final SandboxableApi<?> THREADS =                 new SandboxableApi<>(null, s -> s.addApi(ThreadsApi::new));
    public static final SandboxableApi<?> OBJECTS =                 new SandboxableApi<>(null, s -> s.addApi(ObjectsApi::new));
    public static final SandboxableApi<?> WEB =                     new SandboxableApi<>(null, s -> s.addApi(WebApi::new));

    public static void registerAll() {
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("lua_base"), LUA_BASE);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("lua_package"), LUA_PACKAGE);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("lua_bit32"), LUA_BIT32);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("lua_table"), LUA_TABLE);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("lua_string"), LUA_STRING);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("lua_coroutine"), LUA_COROUTINE);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("lua_math"), LUA_MATH);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("lua_io"), LUA_IO);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("lua_os"), LUA_OS);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("lua_luajava"), LUA_LUAJAVA);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("lua_loadstate"), LUA_LOADSTATE);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("lua_c"), LUA_C);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("luafy"), LUAFY);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("command"), COMMAND);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("server"), SERVER);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("context"), CONTEXT);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("storage"), STORAGE);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("script"), SCRIPT);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("threads"), THREADS);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("objects"), OBJECTS);
        Registry.register(Luafy.Registries.API_REGISTRY, Luafy.id("web"), WEB);
    }



    private static List<SandboxableApi<?>> parseStrategy(Strategy strategy) {
        List<String> m;

        if (strategy.blacklist) {
            m = Luafy.Registries.API_REGISTRY.getIds().stream().map(Identifier::toString).toList();
            List<String> removed = new ArrayList<>();
            for (var e : m) {
                if (!strategy.apis.contains(e)) removed.add(e);
            }
            m = removed;
        } else {
            m = strategy.apis;
        }

        List<SandboxableApi<?>> apis = new ArrayList<>(Luafy.Registries.API_REGISTRY.stream().filter(SandboxableApi::alwaysLoads).toList());

        for (Identifier id : m.stream().map(Identifier::new).toList()) {
            SandboxableApi<?> api = Luafy.Registries.API_REGISTRY.get(id);
            if (api != null) {
                apis.add(api);
            }
        }


        return apis;
    }

    public static void applyAbstractSandbox(AbstractScript<?> script) {
        List<SandboxableApi<?>> apis = parseStrategy(LuafyConfig.GLOBAL_CONFIG.getStrategy());
        for (var api : apis) {
            api.addTo(script);
        }
    }
}
