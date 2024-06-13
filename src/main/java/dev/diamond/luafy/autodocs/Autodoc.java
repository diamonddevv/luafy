package dev.diamond.luafy.autodocs;

import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import dev.diamond.luafy.script.registry.callback.ScriptCallbackEvent;
import dev.diamond.luafy.script.registry.lang.ScriptLanguage;

public interface Autodoc<F, L> {
    F getBlankFormat();
    L getEmptyList();
    byte[] completedToBytes(F format);

    void addSection(F head, F section, String key);
    void addList(F head, L list, String key);

    void addTypedApi(AbstractTypedScriptApi typedApi, L list);
    void addTypedObject(AbstractTypedScriptObject<?> obj, L list);
    void addEvent(ScriptCallbackEvent event, L list);
    void addLang(ScriptLanguage<?> lang, L list);

    String getFilename();
}
