package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.api.CommandApi;
import dev.diamond.luafy.script.old.LuafyLua;
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

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow public abstract ServerCommandSource getCommandSource();


    @Shadow @Nullable public abstract ServerWorld getWorld(RegistryKey<World> key);

    @Unique private boolean lastIsDay = true;
    @Unique private boolean isDay = true;

    @Inject(method = "reloadResources", at = @At("TAIL"))
    public void luafy$runLoadCallbacks(Collection<String> dataPacks, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        ScriptManager.executeEventCallbacks(ScriptManager.CallbackEvent.LOAD, getCommandSource(), null);
    }


    @Inject(method = "tickWorlds", at = @At("HEAD"))
    public void luafy$runTickCallbacks(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {

        if (getWorld(ServerWorld.OVERWORLD) != null) {
            lastIsDay = isDay;
            isDay = getWorld(ServerWorld.OVERWORLD).isDay();
        }

        ScriptManager.executeEventCallbacks(ScriptManager.CallbackEvent.TICK, getCommandSource(), null);

        if (!lastIsDay && isDay) {
            ScriptManager.executeEventCallbacks(ScriptManager.CallbackEvent.ON_DAY_START, getCommandSource(), null);
        }

        if (lastIsDay && !isDay) {
            ScriptManager.executeEventCallbacks(ScriptManager.CallbackEvent.ON_NIGHTFALL, getCommandSource(), null);
        }

    }

//    @Inject(method = "tick", at = @At("TAIL"))
//    private void luafy$executeCachedCommands(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
//        CommandApi.COMMAND_QUEUE.forEach((c) -> c.accept(null));
//        CommandApi.COMMAND_QUEUE.clear();
//    }


    @Inject(method = "startServer", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static <S extends MinecraftServer> void luafy$createServerThreadReferenceAndStartScriptThread(
            Function<Thread, S> serverFactory, CallbackInfoReturnable<S> cir, AtomicReference atomicReference,
            Thread thread, MinecraftServer minecraftServer) {
        ScriptManager.startScriptThread();
        ScriptManager.setServerThread(thread);
    }
}
