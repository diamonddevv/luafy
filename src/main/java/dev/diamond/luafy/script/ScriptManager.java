package dev.diamond.luafy.script;

import com.mojang.brigadier.ParseResults;
import dev.diamond.luafy.script.abstraction.lang.AbstractScript;
import dev.diamond.luafy.util.HexId;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashMap;

public class ScriptManager {

    public enum ExplicitType {
        DOUBLE("NUM"), STRING("STR"), BOOLEAN("BOOL"), OBJECT("OBJ"), LIST("LIST");

        public String str;
        ExplicitType(String str) {
            this.str = str;
        }
    }

    public static class Caches {
        public static HashMap<HexId, ParseResults<ServerCommandSource>> PREPARSED_COMMANDS = new HashMap<>();
    }


    public static final HashMap<String, AbstractScript<?, ?, ?>> SCRIPTS = new HashMap<>();
    public static HashMap<String, SandboxStrategies.Strategy> SANDBOX_STRATEGIES = new HashMap<>();



    public static boolean execute(String script, ServerCommandSource src) {
        if (!has(script)) return false;

        SCRIPTS.get(script).execute(src, null);
        return true;
    }

    public static boolean has(String script) {
        return SCRIPTS.containsKey(script);
    }

    public static AbstractScript<?, ?, ?> get(String script) {
        return SCRIPTS.get(script);
    }
}
