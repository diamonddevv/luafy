package dev.diamond.luafy.script.abstraction;

import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;

public interface AdaptableFunction {
    Object call(AbstractBaseValue<?, ?>[] args);
}
