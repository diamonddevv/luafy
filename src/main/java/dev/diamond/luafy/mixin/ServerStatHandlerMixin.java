package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.api.obj.PlayerEntityScriptObject;
import dev.diamond.luafy.script.callback.ScriptCallbacks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(ServerStatHandler.class)
public class ServerStatHandlerMixin {
    @Inject(method = "setStat", at = @At("HEAD"))
    private void luafy$invokeStatChangedCallbackEvents(PlayerEntity player, Stat<?> stat, int value, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity spe) {
            ScriptManager.executeEventCallbacks(ScriptCallbacks.ON_STAT_CHANGED, player.getCommandSource().withLevel(2), (v) -> {
                HashMap<String, Object> ctx = new HashMap<>();
                ctx.put("player", new PlayerEntityScriptObject(spe));

                ctx.put("stat", stat.getValue().toString());
                ctx.put("was", ((ServerPlayerEntity) player).getStatHandler().getStat(stat));
                ctx.put("now", value);

                return ctx;
            });
        }
    }
}
