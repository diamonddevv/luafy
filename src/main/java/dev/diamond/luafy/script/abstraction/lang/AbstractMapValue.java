package dev.diamond.luafy.script.abstraction.lang;

public abstract class AbstractMapValue
        <
                LangValue,
                Self extends AbstractMapValue<LangValue, Self, MapValue, FuncValue, BaseValue>,
                MapValue extends LangValue,
                FuncValue extends AbstractFunctionValue<LangValue, FuncValue, ? extends LangValue, Self, BaseValue>, // VSFMB
                BaseValue extends AbstractBaseValue<LangValue, FuncValue, Self>
                >
        extends AbstractBaseValue<MapValue, FuncValue, Self> {

    public AbstractMapValue(MapValue value) {
        super(value);
    }
}
