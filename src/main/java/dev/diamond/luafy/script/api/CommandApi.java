package dev.diamond.luafy.script.api;

import dev.diamond.luafy.script.abstraction.lang.AbstractBaseValue;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.script.abstraction.AbstractScriptApi;
import dev.diamond.luafy.script.abstraction.AdaptableFunction;

import java.util.HashMap;

public class CommandApi extends AbstractScriptApi {


    public CommandApi(AbstractScript<?, ?, ?> script) {
        super(script, "command");
    }

    @Override
    public HashMap<String, AdaptableFunction> getFunctions() {
        HashMap<String, AdaptableFunction> f = new HashMap<>();

        f.put("execute", new AdaptableExecuteFunc());

        return f;
    }


    public class AdaptableExecuteFunc implements AdaptableFunction {

        @Override
        public Object call(AbstractBaseValue<?, ?, ?>[] args) {

            var parsed = dev.diamond.luafy.script.old.api.CommandApi.parseCommand(args[0].asString(), script.source);
            dev.diamond.luafy.script.old.api.CommandApi.executeCommand(parsed, script.source);

            return null;
        }
    }
}
