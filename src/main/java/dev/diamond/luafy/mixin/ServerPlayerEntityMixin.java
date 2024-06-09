package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.api.obj.entity.PlayerEntityScriptObject;
import dev.diamond.luafy.script.registry.callback.ScriptCallbacks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends LivingEntity {
    protected ServerPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "swingHand", at = @At("HEAD"))
    private void luafy$invokeTryAttackEventCallbacks(Hand hand, CallbackInfo ci) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.HAND_SWINGS, () -> this.getCommandSource().withLevel(2),
                new PlayerEntityScriptObject((ServerPlayerEntity)(Object)this),
                this.preferredHand == hand
        );
    }



    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void luafy$invokeDisconnectEventCallbacks(CallbackInfo ci) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.DISCONNECTS_FROM_SERVER, () -> this.getCommandSource().withLevel(2),
            new PlayerEntityScriptObject((ServerPlayerEntity)(Object)this)
        );
    }



}
