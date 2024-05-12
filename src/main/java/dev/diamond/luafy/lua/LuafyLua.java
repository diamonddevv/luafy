package dev.diamond.luafy.lua;


import com.google.gson.annotations.SerializedName;
import dev.diamond.luafy.Luafy;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class LuafyLua {

    public static class ArgTypes {
        public static final String
                NUMBER = "NUM", STRING = "STR", TABLE = "OBJ", BOOL = "BOOL", LIST = "LIST";
    }

    public static HashMap<String, LuaScript> LUA_SCRIPTS = new HashMap<>();

    public static List<CallbackScriptBean> CALLBACK_SCRIPTS = new ArrayList<>();

    public static void executeScript(String scriptId, ServerCommandSource src, @Nullable LuaTable ctx) {
        if (LuafyLua.LUA_SCRIPTS.get(scriptId) != null) {
            LuafyLua.LUA_SCRIPTS.get(scriptId).execute(src, ctx);
        }
    }

    public static class CallbackScriptBean {
        @SerializedName("advancements")
        public List<IdentifierCallbackBean> advancementCallbacks;

        @SerializedName("load")
        public ScriptsCallbackBean loadCallbacks;

        @SerializedName("tick")
        public ScriptsCallbackBean tickCallbacks;

        //@SerializedName("entity_context")
        //public List<WithEntityContextCallbackBean> entityCtx;
    }

    public static class WithEntityContextCallbackBean {
        @SerializedName("entity_predicate")
        public String predicate_path;

        @SerializedName("on_death")
        public List<String> onDeath;

        @SerializedName("on_takes_damage")
        public List<String> onDamage;

        @SerializedName("on_deals_damage")
        public List<String> onDealsDamage;

        @SerializedName("on_kills_other")
        public List<String> onKillsOther;

        @SerializedName("tick")
        public List<String> tick;
    }

    public static class ScriptsCallbackBean {
        @SerializedName("scripts")
        public List<String> scriptIds;
    }

    public static class IdentifierCallbackBean {
        @SerializedName("id")
        public String id;

        @SerializedName("scripts")
        public List<String> scriptIds;
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
