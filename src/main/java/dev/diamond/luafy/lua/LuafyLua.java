package dev.diamond.luafy.lua;


import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.ParseResults;
import dev.diamond.luafy.Luafy;
import dev.diamond.luafy.util.LuafyMath;
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
import org.luaj.vm2.LuaValue;

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


    public static class HexId extends LuaValue {

        public final String stringId;
        public final long longId;

        private HexId(String stringId, long longId) {
            this.stringId = stringId;
            this.longId = longId;
            System.out.println(stringId);
        }

        public String get() {
            return stringId;
        }

        public boolean matches(HexId other) {
            return longId == other.longId;
        }


        @Override
        public int type() {
            return TSTRING;
        }

        @Override
        public String typename() {
            return "string";
        }

        @Override
        public String tojstring() {
            return get();
        }

        @Override
        public String toString() {
            return stringId;
        }

        public static HexId makeNewUnique(Collection<HexId> others) {
            HexId id = null;
            while (id == null || others.stream().anyMatch(id::matches)) {
                id = makeNew(8);
            }
            return id;
        }

        private static HexId makeNew(int digits) {
            long lim = LuafyMath.longpow(16, digits);
            Random random = new Random();
            long l = random.nextLong(0, lim);
            return new HexId(Long.toHexString(l), l);
        }

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
