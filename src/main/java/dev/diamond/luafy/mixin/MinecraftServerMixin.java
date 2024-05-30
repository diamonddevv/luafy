package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.registry.callback.ScriptCallbacks;
import dev.diamond.luafy.util.RemovalMarkedRunnable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow public abstract ServerCommandSource getCommandSource();


    @Shadow @Nullable public abstract ServerWorld getWorld(RegistryKey<World> key);

    @Unique private boolean lastIsDay = true;
    @Unique private boolean isDay = true;

    @Inject(method = "reloadResources", at = @At("TAIL"))
    public void luafy$runLoadCallbacks(Collection<String> dataPacks, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.LOAD, this::getCommandSource, null);
    }


    @Inject(method = "tickWorlds", at = @At("HEAD"))
    public void luafy$runTickCallbacks(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {

        if (getWorld(ServerWorld.OVERWORLD) != null) {
            lastIsDay = isDay;
            isDay = getWorld(ServerWorld.OVERWORLD).isDay();
        }

        ScriptManager.executeEventCallbacks(ScriptCallbacks.TICK, this::getCommandSource, null);

        if (!lastIsDay && isDay) {
            ScriptManager.executeEventCallbacks(ScriptCallbacks.ON_DAY_START, this::getCommandSource, null);
        }

        if (lastIsDay && !isDay) {
            ScriptManager.executeEventCallbacks(ScriptCallbacks.ON_NIGHT_START, this::getCommandSource, null);
        }

    }

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tick(Ljava/util/function/BooleanSupplier;)V"))
    private void luafy$executeServerExecutions(CallbackInfo ci) {
        if (!ScriptManager.SERVER_THREAD_EXECUTIONS.isEmpty()) {
            ScriptManager.SERVER_THREAD_EXECUTIONS.forEach(RemovalMarkedRunnable::run);
        }
        ScriptManager.SERVER_THREAD_EXECUTIONS.removeIf(RemovalMarkedRunnable::markedForRemoval);
    }


    @Inject(method = "startServer", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static <S extends MinecraftServer> void luafy$startScriptThreadAndRunLoad(
            Function<Thread, S> serverFactory, CallbackInfoReturnable<S> cir, AtomicReference atomicReference,
            Thread thread, MinecraftServer minecraftServer) {
        ScriptManager.startScriptThread();
        ScriptManager.executeEventCallbacks(ScriptCallbacks.LOAD, minecraftServer::getCommandSource, null);
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void luafy$invokeServerCloseCallbacks(CallbackInfo ci) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.SERVER_CLOSES, this::getCommandSource, null);
    }
}
