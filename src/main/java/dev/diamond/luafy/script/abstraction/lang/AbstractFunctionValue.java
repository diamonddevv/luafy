package dev.diamond.luafy.script.abstraction.lang;

public abstract class AbstractFunctionValue
        <
                LangValue,
                Self extends AbstractFunctionValue<LangValue, Self, FuncValue, BaseValue>,
                FuncValue extends LangValue,
                BaseValue extends AbstractBaseValue<LangValue, Self, BaseValue>
                >
        extends AbstractBaseValue<FuncValue, Self, BaseValue> {

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
