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

            builder.append("\n\t\t- ");
            builder.append(kvp.getValue().description().orElse("<No Description>")); // Add Desc
            builder.append("\n"); // Add Desc

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
        static TypeData of(AdaptableFunction a, Optional<Class<?>> r, NamedParam[] p, NamedParam[] op, Optional<String> d) {
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
                @Override public Optional<String> description() {
                    return d;
                }
            };
        }

        AdaptableFunction function();
        Optional<Class<?>> returnType();
        NamedParam[] params();
        NamedParam[] optionalParams();
        Optional<String> description();
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

        private void add_master(String functionName, AdaptableFunction function, @Nullable Class<?> returnType, @Nullable NamedParam[] params, @Nullable NamedParam[] optionalParams, String d) {

            hash.put(functionName, TypeData.of(
                    function,
                    returnType == null ? Optional.empty() : Optional.of(returnType),
                    params == null ? new NamedParam[0] : params,
                    optionalParams == null ? new NamedParam[0] : optionalParams,
                    Optional.ofNullable(d)
            ));

            fhash.put(functionName, function);
        }

        public void add_Void(String functionName, AdaptableFunction function, NamedParam... params) {
            add_master(functionName, function, null, params, null, null);
        }
        public void add_Void_NoParams(String functionName, AdaptableFunction function) {
            add_master(functionName, function, null, null, null, null);
        }
        public void add_Void_OptionalParams(String functionName, AdaptableFunction function, NamedParam[] optionalParams, @Nullable NamedParam... params) {
            add_master(functionName, function, null, params, optionalParams, null);
        }
        public void add_Void_NoParams_OptionalParams(String functionName, AdaptableFunction function, NamedParam... optionalParams) {
            add_master(functionName, function, null, null, optionalParams, null);
        }
        public void add(String functionName, AdaptableFunction function, Class<?> returnType, NamedParam... params) {
            add_master(functionName, function, returnType, params, null, null);
        }
        public void add_NoParams(String functionName, AdaptableFunction function, Class<?> returnType) {
            add_master(functionName, function, returnType, null, null, null);
        }
        public void add_OptionalParams(String functionName, AdaptableFunction function, Class<?> returnType, NamedParam[] optionalParams, @Nullable NamedParam... params) {
            add_master(functionName, function, returnType, params, optionalParams, null);
        }
        public void add_NoParams_OptionalParams(String functionName, AdaptableFunction function, Class<?> returnType, NamedParam... optionalParams) {
            add_master(functionName, function, returnType, null, optionalParams, null);
        }
        public void add_Void_Desc(String functionName, AdaptableFunction function, String desc, NamedParam... params) {
            add_master(functionName, function, null, params, null, desc);
        }
        public void add_Void_NoParams_Desc(String functionName, AdaptableFunction function, String desc) {
            add_master(functionName, function, null, null, null, desc);
        }
        public void add_Void_OptionalParams_Desc(String functionName, AdaptableFunction function, String desc, NamedParam[] optionalParams, @Nullable NamedParam... params) {
            add_master(functionName, function, null, params, optionalParams, desc);
        }
        public void add_Void_NoParams_OptionalParams_Desc(String functionName, AdaptableFunction function, String desc, NamedParam... optionalParams) {
            add_master(functionName, function, null, null, optionalParams, desc);
        }
        public void add_Desc(String functionName, AdaptableFunction function, String desc, Class<?> returnType, NamedParam... params) {
            add_master(functionName, function, returnType, params, null, desc);
        }
        public void add_NoParams_Desc(String functionName, AdaptableFunction function, String desc, Class<?> returnType) {
            add_master(functionName, function, returnType, null, null, desc);
        }
        public void add_OptionalParams_Desc(String functionName, AdaptableFunction function, String desc, Class<?> returnType, NamedParam[] optionalParams, @Nullable NamedParam... params) {
            add_master(functionName, function, returnType, params, optionalParams, desc);
        }
        public void add_NoParams_OptionalParams_Desc(String functionName, AdaptableFunction function, String desc, Class<?> returnType, NamedParam... optionalParams) {
            add_master(functionName, function, returnType, null, optionalParams, desc);
        }




        protected HashMap<String, AdaptableFunction> getFunctionHash() {
            return fhash;
        }
        public LinkedHashMap<String, TypeData> getHash() {
            return hash;
        }
    }
}
