package dev.diamond.luafy.script.registry.objects;

import dev.diamond.luafy.script.abstraction.obj.IScriptObject;

public interface ScriptObjectFactory<T extends IScriptObject> {
    T create(Object... params);
    Class<T> representedClass();


    static <K extends IScriptObject> ScriptObjectFactory<K> of(Class<K> clazz, FunctionalInterfaceOfFactory<K> fiof) {
        return new ScriptObjectFactory<>() {
            @Override
            public K create(Object... params) {
                return fiof.create(params);
            }

            @Override
            public Class<K> representedClass() {
                return clazz;
            }
        };
    }

    @FunctionalInterface
    interface FunctionalInterfaceOfFactory<T> {
        T create(Object... params);
    }
}
