package dev.diamond.luafy.script.abstraction.api;

import dev.diamond.luafy.script.abstraction.lang.AbstractScript;

@FunctionalInterface
public interface ApiProvider {
    AbstractScriptApi provide(AbstractScript<?> script);
}
