package dev.diamond.luafy.script.abstraction.lang;

import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.api.ApiProvider;
import dev.diamond.luafy.script.registry.lang.ScriptLanguage;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashMap;

/**
 * Base class for all scripts.
 */
public abstract class AbstractScript
        <
                BaseValue extends AbstractBaseValue<?, BaseValue>
                > {

    public ServerCommandSource source;
    public HashMap<?, ?> contextMap;
    public HashMap<?, ?> outContextMap;
    public String name;

    public BaseValue execute(ServerCommandSource source, HashMap<?, ?> contextMap) {
        this.source = FunctionCommand.createFunctionCommandSource(source);
        this.contextMap = contextMap;
        return this.executeScript();
    }

    public BaseValue executeFunction(ServerCommandSource source, HashMap<?, ?> contextMap, String functionName, BaseValue[] params) {
        this.source = FunctionCommand.createFunctionCommandSource(source);
        this.contextMap = contextMap;
        return this.executeScriptFunction(functionName, params);
    }

    public abstract BaseValue executeScript();

    public abstract BaseValue executeScriptFunction(String functionName, BaseValue[] params);

    /**
     *
     * takes abstract functions for an API and adapts them to a scripting language.
     *
     * <br>
     * <br>
     *
     * <pre>
     * implementations must:
     * - add each AdaptableFunction from api by:
     *     - taking LangValues and convert them to BaseValues
     *     - calling AdaptableFunction method
     *     - converting 'call' return value to LangValue
     * </pre>
     *
     * @see AdaptableFunction
     *
     * @param api provided api
     */
    public abstract void addApi(ApiProvider api);

    public abstract BaseValue getNullBaseValue();

    public abstract ScriptLanguage<?> getLanguage();
}
