package dev.diamond.luafy.script.registry.lang;

import dev.diamond.luafy.script.abstraction.lang.AbstractScript;

public abstract class ScriptLanguage<T extends AbstractScript<?>> {

    public ScriptLanguage() {}

    /**
     * @return An array of case-insensitive file extensions. Don't include the period; for example, "lua", not ".lua".
     */
    public abstract String[] getFileExtensions();

    public abstract T readScript(String scriptCode);
}
