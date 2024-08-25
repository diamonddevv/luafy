package dev.diamond.luafy.script.abstraction;

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



    interface TypeData {
        static TypeData of(AdaptableFunction a, Optional<NamedParam.NamedParamClassWrapper<?>> r, NamedParam[] p, NamedParam[] op, Optional<String> d) {
            return new TypeData() {
                @Override public AdaptableFunction function() {
                    return a;
                }
                @Override public Optional<NamedParam.NamedParamClassWrapper<?>> returnType() {
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
        Optional<NamedParam.NamedParamClassWrapper<?>> returnType();
        NamedParam[] params();
        NamedParam[] optionalParams();
        Optional<String> description();
    }


    class TypedFunctionList {
        private final LinkedHashMap<String, TypeData> hash;
        private final HashMap<String, AdaptableFunction> fhash;

        public TypedFunctionList() {
            this.fhash = new HashMap<>();
            this.hash = new LinkedHashMap<>();
        }

        private void add_master(String functionName, AdaptableFunction function, @Nullable NamedParam.NamedParamClassWrapper<?> returnType, @Nullable NamedParam[] params, @Nullable NamedParam[] optionalParams, String d) {

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
            add_master(functionName, function, new NamedParam.NamedParamClassWrapper<>(returnType), params, null, null);
        }
        public void add_NoParams(String functionName, AdaptableFunction function, Class<?> returnType) {
            add_master(functionName, function, new NamedParam.NamedParamClassWrapper<>(returnType), null, null, null);
        }
        public void add_OptionalParams(String functionName, AdaptableFunction function, Class<?> returnType, NamedParam[] optionalParams, @Nullable NamedParam... params) {
            add_master(functionName, function, new NamedParam.NamedParamClassWrapper<>(returnType), params, optionalParams, null);
        }
        public void add_NoParams_OptionalParams(String functionName, AdaptableFunction function, Class<?> returnType, NamedParam... optionalParams) {
            add_master(functionName, function, new NamedParam.NamedParamClassWrapper<>(returnType), null, optionalParams, null);
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
            add_master(functionName, function, new NamedParam.NamedParamClassWrapper<>(returnType), params, null, desc);
        }
        public void add_NoParams_Desc(String functionName, AdaptableFunction function, String desc, Class<?> returnType) {
            add_master(functionName, function, new NamedParam.NamedParamClassWrapper<>(returnType), null, null, desc);
        }
        public void add_OptionalParams_Desc(String functionName, AdaptableFunction function, String desc, Class<?> returnType, NamedParam[] optionalParams, @Nullable NamedParam... params) {
            add_master(functionName, function, new NamedParam.NamedParamClassWrapper<>(returnType), params, optionalParams, desc);
        }
        public void add_NoParams_OptionalParams_Desc(String functionName, AdaptableFunction function, String desc, Class<?> returnType, NamedParam... optionalParams) {
            add_master(functionName, function, new NamedParam.NamedParamClassWrapper<>(returnType), null, optionalParams, desc);
        }





        public void add_NamedParamClassReturn(String functionName, AdaptableFunction function, NamedParam.NamedParamClassWrapper<?> returnType, NamedParam... params) {
            add_master(functionName, function, returnType, params, null, null);
        }
        public void add_NamedParamClassReturn_NoParams(String functionName, AdaptableFunction function, NamedParam.NamedParamClassWrapper<?> returnType) {
            add_master(functionName, function, returnType, null, null, null);
        }
        public void add_NamedParamClassReturn_OptionalParams(String functionName, AdaptableFunction function, NamedParam.NamedParamClassWrapper<?> returnType, NamedParam[] optionalParams, @Nullable NamedParam... params) {
            add_master(functionName, function, returnType, params, optionalParams, null);
        }
        public void add_NamedParamClassReturn_NoParams_OptionalParams(String functionName, AdaptableFunction function, NamedParam.NamedParamClassWrapper<?> returnType, NamedParam... optionalParams) {
            add_master(functionName, function, returnType, null, optionalParams, null);
        }
        public void add_NamedParamClassReturn_Desc(String functionName, AdaptableFunction function, String desc, NamedParam.NamedParamClassWrapper<?> returnType, NamedParam... params) {
            add_master(functionName, function, returnType, params, null, desc);
        }
        public void addNamedParamClassReturn_NoParams_Desc(String functionName, AdaptableFunction function, String desc, NamedParam.NamedParamClassWrapper<?> returnType) {
            add_master(functionName, function, returnType, null, null, desc);
        }
        public void add_NamedParamClassReturn_OptionalParams_Desc(String functionName, AdaptableFunction function, String desc, NamedParam.NamedParamClassWrapper<?> returnType, NamedParam[] optionalParams, @Nullable NamedParam... params) {
            add_master(functionName, function, returnType, params, optionalParams, desc);
        }
        public void add_NamedParamClassReturn_NoParams_OptionalParams_Desc(String functionName, AdaptableFunction function, String desc, NamedParam.NamedParamClassWrapper<?> returnType, NamedParam... optionalParams) {
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
