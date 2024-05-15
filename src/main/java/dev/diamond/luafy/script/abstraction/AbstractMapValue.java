package dev.diamond.luafy.script.abstraction;

public abstract class AbstractMapValue
        <
                V,
                S extends AbstractMapValue<V, S, M, F, B>,
                M extends V,
                F extends AbstractFunctionValue<V, F, ? extends V, S, B>, // VSFMB
                B extends AbstractBaseValue<V, F, S>
                >
        extends AbstractBaseValue<M, F, S> {

    public AbstractMapValue(M value) {
        super(value);
    }
}
