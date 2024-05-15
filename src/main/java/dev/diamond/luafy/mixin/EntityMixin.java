package dev.diamond.luafy.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin{


    @Inject(method = "tick",
            at = @At("HEAD"))
    private void luafy$runEntityCtxTickCallbacks(CallbackInfo ci) {
//        // this is executed after the check if all criteria are completed
//
//        for (var callbacks : LuafyLua.CALLBACK_SCRIPTS) {
//            if (callbacks.entityCtx != null) {
//                for (var entity : callbacks.entityCtx) {
//
//                    if (LuafyLua.getAndTestPredicate(entity.predicate_path, (Entity)(Object)this)) {
//                        for (String script : entity.tick) {
//                            ServerCommandSource source = entity.getCommandSource().withSilent().withLevel(2);
//                            LuafyLua.executeScript(script, source, null);
//                        }
//                    }
//                }
//            }
//        }
    }
}
