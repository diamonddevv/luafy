package dev.diamond.luafy.script.registry.sandbox;

import com.google.gson.annotations.SerializedName;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.registry.lang.ScriptLanguage;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class NonSandboxableApi<T extends AbstractScript<?>> extends SandboxableApi<T> {
    public NonSandboxableApi(@Nullable ScriptLanguage<T> specificLanguage, Consumer<AbstractScript<?>> addToScriptConsumer) {
        super(specificLanguage, addToScriptConsumer);
    }

    @Override
    public boolean alwaysLoads() {
        return true;
    }
}
