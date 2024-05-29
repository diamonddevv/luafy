package dev.diamond.luafy.script.abstraction;

import dev.diamond.luafy.script.ScriptManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashMap;

public abstract class ScriptExecution {

    protected boolean markedForRemoval = false;
    private ScriptExecution() {}
    public abstract void execute();
    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public static ScriptExecution of(String scriptId, ServerCommandSource source, HashMap<?, ?> ctx, String caller) {
        return new ScriptExecution() {
            @Override
            public void execute() {

                ScriptManager.executeCurrentThread(scriptId, source, ctx, caller);
                this.markedForRemoval = true;
            }
        };
    }
}
