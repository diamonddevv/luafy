package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.api.obj.EntityScriptObject;
import dev.diamond.luafy.script.api.obj.Vec3dScriptObject;
import dev.diamond.luafy.script.callback.ScriptCallbacks;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Shadow @NotNull public abstract MinecraftServer getServer();

    @Inject(method = "emitGameEvent", at = @At("HEAD"))
    private void luafy$invokeGameEventCallbacks(GameEvent event, Vec3d emitterPos, GameEvent.Emitter emitter, CallbackInfo ci) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.ON_GAME_EVENT, this.getServer().getCommandSource().withLevel(2), (v) -> {
            HashMap<String, Object> ctx = new HashMap<>();

            ctx.put("emitter_entity", emitter.sourceEntity() == null ? null : new EntityScriptObject(emitter.sourceEntity()));
            ctx.put("event_id", Registries.GAME_EVENT.getId(event).toString());
            ctx.put("emitter_pos", new Vec3dScriptObject(emitterPos));

            return ctx;
        });
    }

}
