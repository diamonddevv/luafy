package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.api.obj.LivingEntityScriptObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private HashMap<?, ?> buildContext(Void v) {
        HashMap<String, LivingEntityScriptObject> c = new HashMap<>();
        c.put("this", new LivingEntityScriptObject(getThis()));
        c.put("last_attacker", new LivingEntityScriptObject(getThis().getLastAttacker()));
        return c;
    }

    @Unique
    private LivingEntity getThis() {
        return (LivingEntity) (Object) this;
    }


    @Inject(method = "onDamaged", at = @At("HEAD"))
    private void luafy$onHurt_LivingEntityContextCallbacks(DamageSource damageSource, CallbackInfo ci) {
        ScriptManager.executeEventCallbacks(ScriptManager.CallbackEvent.ON_ENTITY_HURTS,
                getThis().getCommandSource().withLevel(2), this::buildContext);
    }

    @Inject(method = "onKilledBy", at = @At("HEAD"))
    private void luafy$onDies_LivingEntityContextCallbacks(LivingEntity adversary, CallbackInfo ci) {
        ScriptManager.executeEventCallbacks(ScriptManager.CallbackEvent.ON_ENTITY_DIES,
                getThis().getCommandSource().withLevel(2), this::buildContext);
    }
}
