package dev.diamond.luafy.script.abstraction;

import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface TypedFunctions {

    void setTypedFunctionList(TypedFunctionList list);
    TypedFunctionList getTypedFunctionList();

    void getTypedFunctions(TypedFunctionList f);

    default HashMap<String, AdaptableFunction> getUntypedFunctions() {
        setTypedFunctionList(new TypedFunctionList());
        getTypedFunctions(getTypedFunctionList());
        return getTypedFunctionList().getFunctionHash();
    }

    default Collection<String> formStringSignatures(String setName, String setDelimiter, boolean tabbed) {
        getUntypedFunctions();
        Collection<String> signatures = new ArrayList<>();

        for (var kvp : getTypedFunctionList().hash.entrySet()) {

            StringBuilder builder = new StringBuilder();

            builder.append(kvp.getKey()); // add method name
            builder.append("(");

            builder.append(addParamsToStringSignature(kvp.getValue().params(), "", "", kvp.getValue().optionalParams().length > 0));
            builder.append(addParamsToStringSignature(kvp.getValue().optionalParams(), "[", "]", false));

            builder.append(") -> ");
            builder.append(kvp.getValue().returnType().map(Class::getSimpleName).orElse("void")); // add return type

            if (tabbed) signatures.add("\t" + builder);
            else signatures.add(builder.toString());

            Luafy.LOGGER.info("[Function Signature Generator]: Added function " + setName + setDelimiter + kvp.getKey());
        }

        return signatures;
    }
    private String addParamsToStringSignature(NamedParam[] params, String prefix, String suffix, boolean commaLastAnyway) {
        StringBuilder b = new StringBuilder();
        int paramCount = params.length;
        int i = 0;
        for (var param : params) {
            b.append(prefix);
            b.append(param.name);
            b.append(": ");
            b.append(param.clazz.getSimpleName());
            b.append(suffix);
            i++;
            if (i < paramCount|| commaLastAnyway) b.append(", ");
        }
        return b.toString();
    }



    interface TypeData {
        static TypeData of(AdaptableFunction a, Optional<Class<?>> r, NamedParam[] p, NamedParam[] op) {
            return new TypeData() {
                @Override public AdaptableFunction function() {
                    return a;
                }
                @Override public Optional<Class<?>> returnType() {
                    return r;
                }
                @Override public NamedParam[] params() {
                    return p;
                }
                @Override public NamedParam[] optionalParams() {
                    return op;
                }
            };
        }

        AdaptableFunction function();
        Optional<Class<?>> returnType();
        NamedParam[] params();
        NamedParam[] optionalParams();
    }

    class NamedParam {
        public final String name;
        public final Class<?> clazz;

        public NamedParam(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public NamedParam(Class<?> clazz) {
                this.name = "";
                this.clazz = clazz;
        }

        public NamedParam(String name) {
            this.name = name;
            this.clazz = Object.class;
        }
    }


    class TypedFunctionList {
        private final LinkedHashMap<String, TypeData> hash;
        private final HashMap<String, AdaptableFunction> fhash;

        public TypedFunctionList() {
            this.fhash = new HashMap<>();
            this.hash = new LinkedHashMap<>();
        }

        private void add_master(String functionName, AdaptableFunction function, @Nullable Class<?> returnType, @Nullable NamedParam[] params, @Nullable NamedParam[] optionalParams) {

            hash.put(functionName, TypeData.of(
                    function,
                    returnType == null ? Optional.empty() : Optional.of(returnType),
                    params == null ? new NamedParam[0] : params,
                    optionalParams == null ? new NamedParam[0] : optionalParams
            ));

            fhash.put(functionName, function);
        }

        public void add_Void(String functionName, AdaptableFunction function, NamedParam... params) {
            add_master(functionName, function, null, params, null);
        }
        public void add_VoidNoParams(String functionName, AdaptableFunction function) {
            add_master(functionName, function, null, null, null);
        }
        public void add_VoidWithOptionalParams(String functionName, AdaptableFunction function, NamedParam[] optionalParams, @Nullable NamedParam... params) {
            add_master(functionName, function, null, params, optionalParams);
        }
        public void add_VoidNoParamsWithOptionalParams(String functionName, AdaptableFunction function, NamedParam... optionalParams) {
            add_master(functionName, function, null, null, optionalParams);
        }
        public void add(String functionName, AdaptableFunction function, Class<?> returnType, NamedParam... params) {
            add_master(functionName, function, returnType, params, null);
        }
        public void add_NoParams(String functionName, AdaptableFunction function, Class<?> returnType) {
            add_master(functionName, function, returnType, null, null);
        }
        public void add_WithOptionalParams(String functionName, AdaptableFunction function, Class<?> returnType, NamedParam[] optionalParams, @Nullable NamedParam... params) {
            add_master(functionName, function, returnType, params, optionalParams);
        }
        public void add_NoParamsWithOptionalParams(String functionName, AdaptableFunction function, Class<?> returnType, NamedParam... optionalParams) {
            add_master(functionName, function, returnType, null, optionalParams);
        }



        protected HashMap<String, AdaptableFunction> getFunctionHash() {
            return fhash;
        }
        public LinkedHashMap<String, TypeData> getHash() {
            return hash;
        }
    }
}
