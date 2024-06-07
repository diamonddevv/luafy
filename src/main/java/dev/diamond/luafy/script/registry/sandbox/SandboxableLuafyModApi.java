package dev.diamond.luafy.script.registry.sandbox;

import dev.diamond.luafy.script.abstraction.api.ApiProvider;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.registry.lang.ScriptLanguage;
import org.jetbrains.annotations.Nullable;

public class SandboxableLuafyModApi extends SandboxableApi<AbstractScript<?>> {
    private final ApiProvider api;

    public SandboxableLuafyModApi(@Nullable ScriptLanguage<AbstractScript<?>> specificLanguage, ApiProvider api) {
        super(specificLanguage, s -> s.addApi(api));
        this.api = api;
    }

    public ApiProvider getApiProvider() {
        return api;
    }
}
