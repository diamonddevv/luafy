package dev.diamond.luafy.script.registry.lang;

import dev.diamond.luafy.util.DescriptionProvider;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;

public abstract class ScriptLanguage<T extends AbstractScript<?>> implements DescriptionProvider {

    public ScriptLanguage() {}

    /**
     * @return An array of case-insensitive file extensions. Don't include the period; for example, "lua", not ".lua".
     */
    public abstract String[] getFileExtensions();

    public abstract T readScript(String scriptCode);

    public String getLanguageDocumentationUrl() {
        return null;
    }

    public String getImplementerCredits() {
        return null;
    }

    public String getLangName() { return getClass().getSimpleName(); }

    @Override
    public String getDescription() {
        return null;
    }
}
