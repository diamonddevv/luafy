package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.api.obj.entity.PlayerEntityScriptObject;
import dev.diamond.luafy.script.registry.callback.ScriptCallbacks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "addExhaustion", at = @At("HEAD"))
    private void luafy$invokeExhaustEventCallbacks(float exhaustion, CallbackInfo ci) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.EXHAUST, () -> this.getCommandSource().withLevel(2),
            new PlayerEntityScriptObject((ServerPlayerEntity)(Object)this),
            exhaustion
        );
    }
}
