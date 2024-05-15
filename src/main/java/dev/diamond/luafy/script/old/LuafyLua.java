package dev.diamond.luafy.script.old;


import com.mojang.brigadier.ParseResults;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.util.HexId;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
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

    public static HashMap<String, Old_LuaScript> LUA_SCRIPTS = new HashMap<>();
    public static List<ScriptCallbacks.CallbackScriptBean> CALLBACK_SCRIPTS = new ArrayList<>();
    public static HashMap<String, SandboxStrategies.Strategy> SANDBOX_STRATEGIES = new HashMap<>();

    public static void executeScript(String scriptId, ServerCommandSource src, @Nullable LuaTable ctx) {
        if (LuafyLua.LUA_SCRIPTS.get(scriptId) != null) {
            LuafyLua.LUA_SCRIPTS.get(scriptId).execute(src, ctx);
        }
    }


    public static boolean getAndTestPredicate(String predicateId, @NotNull Entity entity) {
        String[] splits = predicateId.split(":");
        Identifier identifier = new Identifier(splits[0], splits[1]);

        if (entity.getServer() == null) {
            Luafy.LOGGER.error("Entity server was null");
            return false;
        }

        LootManager lootManager = entity.getServer().getLootManager();
        LootCondition lootCondition = lootManager.getElement(LootDataType.PREDICATES, identifier);
        if (lootCondition == null) {
            Luafy.LOGGER.error("Predicate not found : " + predicateId);
            return false;
        } else {

            LootContextParameterSet.Builder build = new LootContextParameterSet.Builder(entity.getCommandSource().getWorld())
                    .add(LootContextParameters.ORIGIN, entity.getPos())
                    .addOptional(LootContextParameters.THIS_ENTITY, entity);


            if (entity instanceof LivingEntity le) {
                build.addOptional(LootContextParameters.DAMAGE_SOURCE, le.getRecentDamageSource());
                build.addOptional(LootContextParameters.TOOL, le.getActiveItem());

                if (le.getLastAttacker() instanceof ServerPlayerEntity pe) {
                    build.addOptional(LootContextParameters.LAST_DAMAGE_PLAYER, pe);
                }
            }



            LootContext context = (new LootContext.Builder(build.build(LootContextTypes.COMMAND))).build(Optional.empty());

            return lootCondition.test(context);
        }
    }

}
