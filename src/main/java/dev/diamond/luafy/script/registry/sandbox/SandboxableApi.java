package dev.diamond.luafy.script.registry.sandbox;

import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.registry.lang.ScriptLanguage;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SandboxableApi<T extends AbstractScript<?>> {
    private final ScriptLanguage<T> specificLang;
    private final Consumer<AbstractScript<?>> consumer;

    public SandboxableApi(@Nullable ScriptLanguage<T> specificLanguage, Consumer<AbstractScript<?>> addToScriptConsumer) {
        this.specificLang = specificLanguage;
        this.consumer = addToScriptConsumer;
    }

    public void addTo(AbstractScript<?> script) {
        if (specificLang != null) {
            if (specificLang.getClass().isInstance(script.getLanguage())) {
                consumer.accept(script);
            }
        } else {
            consumer.accept(script);
        }
    }

    /**
     * @return true if the script should ALWAYS be loaded, whether it should or shouldn't be excluded.
     */
    public boolean alwaysLoads() {
        return false;
    }
}
