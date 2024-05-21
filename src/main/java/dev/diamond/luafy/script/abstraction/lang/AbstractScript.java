package dev.diamond.luafy.script.abstraction.lang;

import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.api.ApiProvider;
import dev.diamond.luafy.script.abstraction.obj.ScriptObjectProvider;
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

    public BaseValue execute(ServerCommandSource source, HashMap<?, ?> contextMap) {
        this.source = FunctionCommand.createFunctionCommandSource(source);
        this.contextMap = contextMap;
        return this.executeScript();
    }

    public abstract BaseValue executeScript();

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
}
