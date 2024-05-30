package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.api.obj.entity.EntityScriptObject;
import dev.diamond.luafy.script.api.obj.entity.PlayerEntityScriptObject;
import dev.diamond.luafy.script.api.obj.math.Vec3dScriptObject;
import dev.diamond.luafy.script.registry.callback.ScriptCallbacks;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Shadow @NotNull public abstract MinecraftServer getServer();

    @Inject(method = "emitGameEvent", at = @At("HEAD"))
    private void luafy$invokeGameEventCallbacks(GameEvent event, Vec3d emitterPos, GameEvent.Emitter emitter, CallbackInfo ci) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.ON_GAME_EVENT, () -> this.getServer().getCommandSource().withLevel(2), (v) -> {
            v.put("emitter_entity", emitter.sourceEntity() == null ? null : new EntityScriptObject(emitter.sourceEntity()));
            v.put("event_id", Registries.GAME_EVENT.getId(event).toString());
            v.put("emitter_pos", new Vec3dScriptObject(emitterPos));
        });
    }

    @Inject(method = "onPlayerConnected", at = @At("HEAD"))
    private void luafy$invokeConnectEventCallbacks(ServerPlayerEntity player, CallbackInfo ci) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.CONNECTS_TO_SERVER, () -> player.getCommandSource().withLevel(2), v -> {
            v.put("player", new PlayerEntityScriptObject(player));
        });
    }

}
