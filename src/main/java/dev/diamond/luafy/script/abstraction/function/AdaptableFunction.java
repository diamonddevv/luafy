package dev.diamond.luafy.script.abstraction.function;

import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;

@FunctionalInterface
public interface AdaptableFunction {
    Object call(AbstractBaseValue<?, ?>... args);
}
