package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.api.obj.util.AdvancementEntryScriptObject;
import dev.diamond.luafy.script.callback.ScriptCallbacks;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {

    @Shadow private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/AdvancementRewards;apply(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    private void luafy$runAdvancementCallbacks(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        // this is executed after the check if all criteria are completed

        ScriptManager.executeEventCallbacks(ScriptCallbacks.ON_ADVANCEMENT_OBTAINED, owner.getCommandSource().withLevel(2), v -> {
            v.put("advancement", new AdvancementEntryScriptObject(advancement));
        });
    }
}
