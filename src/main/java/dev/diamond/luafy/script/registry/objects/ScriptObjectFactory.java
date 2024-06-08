package dev.diamond.luafy.script.registry.objects;

import dev.diamond.luafy.script.abstraction.obj.IScriptObject;

@FunctionalInterface
public interface ScriptObjectFactory<T extends IScriptObject> {
    T create(Object... params);
}
