package dev.diamond.luafy.script.abstraction;

public abstract class AbstractFunctionValue
        <
                V,
                S extends AbstractFunctionValue<V, S, F, M, B>,
                F extends V,
                M extends AbstractMapValue<V, M, ? extends V, S, B>, // VSMFB
                B extends AbstractBaseValue<V, S, M>
                >
        extends AbstractBaseValue<F, S, M> {

    public AbstractFunctionValue(F value) {
        super(value);
    }

    public abstract B invoke(B[] params);
}
