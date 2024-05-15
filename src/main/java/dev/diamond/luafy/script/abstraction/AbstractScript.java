package dev.diamond.luafy.script.abstraction;

import net.minecraft.server.command.ServerCommandSource;

/**
 * Base class for all scripts.
 *
 * @param <T> Base Type Value
 */
public abstract class AbstractScript
        <
                F extends AbstractFunctionValue<?, F, ?, M, T>,
                M extends AbstractMapValue<?, M, ?, F, T>,
                T extends AbstractBaseValue<?, F, M>

                > {

    public abstract T execute(ServerCommandSource source, M contextMap);
}
