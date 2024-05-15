package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.old.LuafyLua;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {

    @Shadow private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/AdvancementRewards;apply(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    private void luafy$runAdvancementCallbacks(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        // this is executed after the check if all criteria are completed

        for (var callbacks : LuafyLua.CALLBACK_SCRIPTS) {
            if (callbacks.advancementCallbacks != null) {
                for (var advCall : callbacks.advancementCallbacks) {

                    if (Objects.equals(advCall.id, advancement.id().toString()) || Objects.equals(advCall.id, "*")) { // wildcard works
                        for (String script : advCall.scriptIds) {
                            ServerCommandSource source = owner.getCommandSource().withSilent().withLevel(2);
                            LuafyLua.executeScript(script, source, null);
                        }
                    }
                }
            }
        }
    }
}
