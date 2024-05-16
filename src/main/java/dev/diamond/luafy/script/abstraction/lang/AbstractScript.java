package dev.diamond.luafy.script.abstraction.lang;

import dev.diamond.luafy.script.abstraction.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.ApiProvider;
import dev.diamond.luafy.script.old.SandboxStrategies;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Consumer;

/**
 * Base class for all scripts.
 */
public abstract class AbstractScript
        <
                FuncValue extends AbstractFunctionValue<?, FuncValue, ?, MapValue, BaseValue>,
                MapValue extends AbstractMapValue<?, MapValue, ?, FuncValue, BaseValue>,
                BaseValue extends AbstractBaseValue<?, FuncValue, MapValue>

                > {

    public ServerCommandSource source;
    public MapValue contextMap;
    public MapValue outContextMap;

    public BaseValue execute(ServerCommandSource source, MapValue contextMap) {
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



}
