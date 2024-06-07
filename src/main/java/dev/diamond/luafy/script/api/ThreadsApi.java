package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.abstraction.function.AdaptableFunction;
import dev.diamond.luafy.script.abstraction.api.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;

import java.util.HashMap;

public class ThreadsApi extends AbstractScriptApi {
    public ThreadsApi(AbstractScript<?> script) {
        super(script, "threads");
    }

    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        HashMap<String, AdaptableFunction> f = new HashMap<>();

        f.put("run_on_thread", args -> {
            AdaptableFunction function = args[0].asFunction();
            AbstractBaseValue<?, ?>[] callArgs = new AbstractBaseValue<?, ?>[args.length - 1];
            System.arraycopy(args, 1, callArgs, 0, callArgs.length);

            Thread thread = new Thread(null, () -> {
                function.call(callArgs);
            }, "ThreadsApi on_thread Invocation Thread");
            thread.setDaemon(true);
            thread.start();

            return null;
        });

        return f;
    }
}
