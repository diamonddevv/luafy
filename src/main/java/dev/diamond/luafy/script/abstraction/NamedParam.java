package dev.diamond.luafy.script.abstraction;

import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import dev.diamond.luafy.script.registry.objects.ScriptObjectRegistry;
import dev.diamond.luafy.util.DescriptionProvider;

public class NamedParam implements DescriptionProvider {
    public final String name;
    public final String desc;
    public final NamedParamClassWrapper<?> clazz;

    public NamedParam(String name, String desc, Class<?> clazz) {
        this.name = name;
        this.desc = desc;
        this.clazz = new NamedParamClassWrapper<>(clazz);
    }

    public NamedParam(String name, Class<?> clazz) {
        this.name = name;
        this.desc = "";
        this.clazz = new NamedParamClassWrapper<>(clazz);
    }

    public NamedParam(Class<?> clazz) {
        this.name = "";
        this.desc = "";
        this.clazz = new NamedParamClassWrapper<>(clazz);
    }

    public NamedParam(String name) {
        this.name = name;
        this.desc = "";
        this.clazz = new NamedParamClassWrapper<>(Object.class);
    }

    public String getString() {
        return clazz.clazz.getSimpleName();
    }

    @Override
    public String getDescription() {
        return desc;
    }

    public static class FunctionParam extends NamedParam {
        public FunctionParam(String name, NamedParam[] params, NamedParam[] opParams, Class<?> rtn) {
            super(name, AdaptableFunction.class);
            clazz.setFParams(params);
            clazz.setFOpParams(opParams);
            clazz.setFReturn(new NamedParamClassWrapper<>(rtn));

        }
    }

    public static class NamedParamClassWrapper<T> {
        public final Class<T> clazz;
        private final boolean isFunction;

        private NamedParam[] fParams;
        private NamedParam[] fOpParams;
        private NamedParamClassWrapper<?> fReturn;

        public NamedParamClassWrapper(Class<T> clazz) {
            this.clazz = clazz;
            isFunction = clazz == AdaptableFunction.class;

            if (!isFunction) {
                fParams = null;
                fOpParams = null;
                fReturn = null;
            }
        }

        public NamedParamClassWrapper(NamedParam[] p, NamedParam[] op, NamedParamClassWrapper<?> returnValue) {
            this.clazz = (Class<T>) AdaptableFunction.class;
            isFunction = true;

            fParams = p;
            fOpParams = op;
            fReturn = returnValue;
        }

        public String getName() {
            if (IScriptObject.class.isAssignableFrom(clazz)) {
                // oh man this is CURSED
                var provider = ScriptObjectRegistry.getByClass(clazz);
                if (provider.isPresent()) {
                    var iso = provider.get().create(new Object[256]);
                    if (iso instanceof AbstractTypedScriptObject<?> atso) {
                        return atso.getName();
                    }
                }
            }
            return clazz.getSimpleName();
        }


        public boolean isFunction() {
            return isFunction;
        }

        public void setFParams(NamedParam[] params) {
            fParams = params;
        }
        public void setFOpParams(NamedParam[] params) {
            fOpParams = params;
        }
        public void setFReturn(NamedParamClassWrapper<?> param) {
            fReturn = param;
        }


        public NamedParam[] functionParams() {
            return fParams;
        }
        public NamedParam[] functionOptionalParams() {
            return fOpParams;
        }
        public NamedParamClassWrapper<?>   functionReturn() {
            return fReturn;
        }

    }

}
