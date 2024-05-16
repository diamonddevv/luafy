package dev.diamond.luafy.script;

import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashMap;

public class ScriptManager {
    public static final HashMap<String, AbstractScript<?, ?, ?>> SCRIPTS = new HashMap<>();

    public static boolean execute(String script, ServerCommandSource src) {
        SCRIPTS.get(script).execute(src, null);
        return true;
    }
}
