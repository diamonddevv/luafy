package dev.diamond.luafy.util;

@FunctionalInterface
public interface Provider<T> {
    T provide();
}
