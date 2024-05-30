package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.registry.callback.ScriptCallbacks;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {


    @Shadow public int age;

    @Shadow public abstract ServerCommandSource getCommandSource();

    @Inject(method = "tick", at = @At("HEAD"))
    private void luafy$invokeTickingCallbacks(CallbackInfo ci) {
        if (age == 0) {
            ScriptManager.executeEventCallbacks(ScriptCallbacks.SPAWN,
                    () -> getCommandSource().withLevel(2), null);
        }

        ScriptManager.executeEventCallbacks(ScriptCallbacks.TICK_ENTITY,
                () -> getCommandSource().withLevel(2), ctx -> {
                    ctx.put("age", age);
                });
    }
}
