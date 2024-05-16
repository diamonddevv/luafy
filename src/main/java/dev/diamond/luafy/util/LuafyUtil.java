package dev.diamond.luafy.util;

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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class LuafyUtil {
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
