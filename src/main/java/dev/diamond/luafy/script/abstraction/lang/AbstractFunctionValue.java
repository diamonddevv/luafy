package dev.diamond.luafy.script.abstraction.lang;

public abstract class AbstractFunctionValue
        <
                LangValue,
                Self extends AbstractFunctionValue<LangValue, Self, FuncValue, MapValue, BaseValue>,
                FuncValue extends LangValue,
                MapValue extends AbstractMapValue<LangValue, MapValue, ? extends LangValue, Self, BaseValue>, // VSMFB
                BaseValue extends AbstractBaseValue<LangValue, Self, MapValue>
                >
        extends AbstractBaseValue<FuncValue, Self, MapValue> {

    public AbstractFunctionValue(FuncValue value) {
        super(value);
    }

    /**
     * Executes the FuncValue
     *
     * @param params params passed to function
     * @return value returned by function
     */
    public abstract BaseValue invoke(BaseValue[] params);
}
