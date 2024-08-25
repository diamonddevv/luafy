package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.api.AbstractTypedScriptApi;
import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;

import java.util.Objects;

public class ThreadsApi extends AbstractTypedScriptApi {
    public ThreadsApi(AbstractScript<?> script) {
        super(script, "threads");
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {

        f.add_Void_Desc("run_on_thread", args -> {
            AdaptableFunction function = args[0].asFunction();
            AbstractBaseValue<?, ?>[] callArgs = new AbstractBaseValue<?, ?>[args.length - 1];
            System.arraycopy(args, 1, callArgs, 0, callArgs.length);

            Thread thread = new Thread(null, () -> {
                function.call(callArgs);
            }, "ThreadsApi run_on_thread Invocation Thread");
            thread.setDaemon(true);
            thread.start();

            return null;
        }, "Runs the given function on a seperate thread.",
                new NamedParam.FunctionParam(
                        "function",
                        new NamedParam[] {
                                new NamedParam("params", "Any number of arbitrary parameters from function call on original thread.", Objects.class)
                        },
                        new NamedParam[0],
                        null
                ),
                new NamedParam("params", "Any number of arbitrary parameters to pass to function on new thread.", Objects.class)
        );

    }

    @Override
    public String getDescription() {
        return "Contains functions relating to the execution of code/scripts on other threads.";
    }
}
