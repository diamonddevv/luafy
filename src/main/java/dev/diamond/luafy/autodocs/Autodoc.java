package dev.diamond.luafy.autodocs;

import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import dev.diamond.luafy.script.registry.callback.ScriptCallbackEvent;
import dev.diamond.luafy.script.registry.lang.ScriptLanguage;

public interface Autodoc<F, L> {
    F getBlankFormat();
    L getEmptyList();
    byte[] completedToBytes(F format);
    void addList(F head, L list, String snakeCaseKey, String titleCaseKey);

    default void preApis(L list) {}
    default void preObjects(L list) {}
    default void preLangs(L list) {}
    default void preEvents(L list) {}

    void addTypedApi(AbstractTypedScriptApi typedApi, L list);
    void addTypedObject(AbstractTypedScriptObject<?> obj, L list);
    void addEvent(ScriptCallbackEvent event, L list);
    void addLang(ScriptLanguage<?> lang, L list);

    String getFilename();

}
