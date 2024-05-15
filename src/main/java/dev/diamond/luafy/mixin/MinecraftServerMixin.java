package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.old.LuafyLua;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow public abstract ServerCommandSource getCommandSource();

    @Inject(method = "reloadResources", at = @At("TAIL"))
    public void luafy$runLoadCallbacks(Collection<String> dataPacks, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        for (var callbacks : LuafyLua.CALLBACK_SCRIPTS) {
            if (callbacks.loadCallbacks != null) {
                for (var script : callbacks.loadCallbacks.scriptIds) {
                    LuafyLua.executeScript(script, this.getCommandSource(), null);
                }
            }
        }
    }


    @Inject(method = "tickWorlds", at = @At("HEAD"))
    public void luafy$runTickCallbacks(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        for (var callbacks : LuafyLua.CALLBACK_SCRIPTS) {
            if (callbacks.tickCallbacks != null) {
                for (var script : callbacks.tickCallbacks.scriptIds) {
                    LuafyLua.executeScript(script, this.getCommandSource(), null);
                }
            }
        }
    }
}
