package dev.diamond.luafy.script.abstraction.api;

import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractTypedScriptApi extends AbstractScriptApi {

    public TypedFunctionList typedFunctionList;

    public AbstractTypedScriptApi(AbstractScript<?> script, String name) {
        super(script, name);
    }

    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        typedFunctionList = new TypedFunctionList();
        getTypedFunctions(typedFunctionList);
        return typedFunctionList.getFunctionHash();
    }

    public abstract void getTypedFunctions(TypedFunctionList f);

    public Collection<String> formSignatures(boolean tabbed) {
        getFunctions();
        Collection<String> signatures = new ArrayList<>();

        for (var kvp : typedFunctionList.hash.entrySet()) {
            StringBuilder builder = new StringBuilder();

            builder.append(kvp.getKey()); // add method name
            builder.append("(");

            builder.append(addParamsToSignature(kvp.getValue().params(), "", "", kvp.getValue().optionalParams().length > 0));
            builder.append(addParamsToSignature(kvp.getValue().optionalParams(), "[", "]", false));

            builder.append(") -> ");
            builder.append(kvp.getValue().returnType().map(Class::getSimpleName).orElse("void")); // add return type

            if (tabbed) signatures.add("\t" + builder);
            else signatures.add(builder.toString());
        }

        return signatures;
    }

    private String addParamsToSignature(Class<?>[] params, String prefix, String suffix, boolean commaLastAnyway) {
        StringBuilder b = new StringBuilder();
        int paramCount = params.length;
        int i = 0;
        for (var param : params) {
            b.append(prefix);
            b.append(param.getSimpleName());
            b.append(suffix);
            i++;
            if (i < paramCount|| commaLastAnyway) b.append(", ");
        }
        return b.toString();
    }

    public interface TypeData {
        static TypeData of(AdaptableFunction a, Optional<Class<?>> r, Class<?>[] p, Class<?>[] op) {
            return new TypeData() {
                @Override public AdaptableFunction function() {
                    return a;
                }
                @Override public Optional<Class<?>> returnType() {
                    return r;
                }
                @Override public Class<?>[] params() {
                    return p;
                }
                @Override public Class<?>[] optionalParams() {
                    return op;
                }
            };
        }

        AdaptableFunction function();
        Optional<Class<?>> returnType();
        Class<?>[] params();
        Class<?>[] optionalParams();
    }
    public static class TypedFunctionList {
        private final HashMap<String, TypeData> hash;
        private final HashMap<String, AdaptableFunction> fhash;

        public TypedFunctionList() {
            this.fhash = new HashMap<>();
            this.hash = new HashMap<>();
        }

        private void add_master(String functionName, AdaptableFunction function, @Nullable Class<?> returnType, @Nullable Class<?>[] params, @Nullable Class<?>[] optionalParams) {
            hash.put(functionName, TypeData.of(
                    function,
                    returnType == null ? Optional.empty() : Optional.of(returnType),
                    params == null ? new Class[0] : params,
                    optionalParams == null ? new Class[0] : optionalParams
            ));

            fhash.put(functionName, function);
        }

        public void addVoid(String functionName, AdaptableFunction function, Class<?>[] params) {
            add_master(functionName, function, null, params, null);
        }
        public void addVoidNoParams(String functionName, AdaptableFunction function) {
            add_master(functionName, function, null, null, null);
        }
        public void addVoidWithOptionalParams(String functionName, AdaptableFunction function, Class<?>[] optionalParams, @Nullable Class<?>... params) {
            add_master(functionName, function, null, params, optionalParams);
        }
        public void addVoidNoParamsWithOptionalParams(String functionName, AdaptableFunction function, Class<?>... optionalParams) {
            add_master(functionName, function, null, null, optionalParams);
        }

        public void add(String functionName, AdaptableFunction function, Class<?> returnType, Class<?>... params) {
            add_master(functionName, function, returnType, params, null);
        }
        public void addNoParams(String functionName, AdaptableFunction function, Class<?> returnType) {
            add_master(functionName, function, returnType, null, null);
        }
        public void addWithOptionalParams(String functionName, AdaptableFunction function, Class<?> returnType, Class<?>[] optionalParams, @Nullable Class<?>... params) {
            add_master(functionName, function, returnType, params, optionalParams);
        }
        public void addNoParamsWithOptionalParams(String functionName, AdaptableFunction function, Class<?> returnType, Class<?>... optionalParams) {
            add_master(functionName, function, returnType, null, optionalParams);
        }



        protected HashMap<String, AdaptableFunction> getFunctionHash() {
            return fhash;
        }
    }
}
