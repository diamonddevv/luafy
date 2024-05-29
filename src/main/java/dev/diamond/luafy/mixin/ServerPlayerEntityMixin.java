package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.api.obj.entity.PlayerEntityScriptObject;
import dev.diamond.luafy.script.registry.callback.ScriptCallbacks;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "swingHand", at = @At("HEAD"))
    private void luafy$invokeTryAttackEventCallbacks(Hand hand, CallbackInfo ci) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.TRY_ATTACK, ((Entity) (Object) this).getCommandSource(), v -> {
            v.put("player", new PlayerEntityScriptObject((ServerPlayerEntity)(Object)this));
        });
    }
}
