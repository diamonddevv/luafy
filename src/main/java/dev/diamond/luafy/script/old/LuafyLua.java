package dev.diamond.luafy.script.old;


import com.mojang.brigadier.ParseResults;
import dev.diamond.luafy.script.SandboxStrategies;
import dev.diamond.luafy.script.ScriptCallbacks;
import dev.diamond.luafy.util.HexId;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;

import java.util.*;

public class LuafyLua {

    public static class ArgTypes {
        public static final String
                NUMBER = "NUM", STRING = "STR", TABLE = "OBJ", BOOL = "BOOL", LIST = "LIST";
    }

    public static class ScriptManagements {
        public static HashMap<HexId, ParseResults<ServerCommandSource>> PREPARSED_COMMANDS_CACHE = new HashMap<>();
        public static HashMap<HexId, Collection<? extends Entity>> ENTITY_GROUP_CACHE = new HashMap<>();
    }

}
