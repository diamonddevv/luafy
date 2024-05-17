package dev.diamond.luafy.script.abstraction;

import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;

@FunctionalInterface
public interface LangTypeAdapter {
    AbstractBaseValue<?, ?, ?> adapt(Object obj);
}
