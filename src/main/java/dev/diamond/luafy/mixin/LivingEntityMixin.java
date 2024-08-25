package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.api.obj.entity.LivingEntityScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.item.ItemStackScriptObject;
import dev.diamond.luafy.script.registry.callback.ScriptCallbacks;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {


    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private LivingEntityScriptObject attackerSo(DamageSource s) {
        if (s.getAttacker() instanceof LivingEntity le) {
            return new LivingEntityScriptObject(le);
        } else return null;
    }

    @Unique
    private LivingEntityScriptObject thisSo() {
        return new LivingEntityScriptObject(getThis());
    }

    @Unique
    private LivingEntity getThis() {
        return (LivingEntity) (Object) this;
    }


    @Inject(method = "damage", at = @At("HEAD"))
    private void luafy$invokeOnHurtCallbacks(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.ON_ENTITY_HURTS,
                () -> getThis().getCommandSource().withLevel(2),

                attackerSo(source),
                thisSo(),
                amount,
                getThis().getDamageSources().registry.getId(source.getType()).toString()
        );
    }

    @Inject(method = "onKilledBy", at = @At("HEAD"))
    private void luafy$invokeOnDieCallbacks(LivingEntity adversary, CallbackInfo ci) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.ON_ENTITY_DIES,
                () -> getThis().getCommandSource().withLevel(2),
                new LivingEntityScriptObject(adversary), thisSo()
        );
    }

    @Inject(method = "tryAttack", at = @At("HEAD"))
    private void luafy$invokeTryAttackCallbacks(Entity target, CallbackInfoReturnable<Boolean> cir) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.TRY_ATTACK,
                () -> getThis().getCommandSource().withLevel(2),
                thisSo()
        );
    }


    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)Z", at = @At("HEAD"))
    private void luafy$invokeAddEffectCallbacks(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.EFFECT_APPLIED,
                () -> getThis().getCommandSource().withLevel(2),
                thisSo(),
                Registries.STATUS_EFFECT.getId(effect.getEffectType().value()).toString(),
                effect.getDuration(),
                effect.getAmplifier()
        );
    }

    @Inject(method = "removeStatusEffect", at = @At("HEAD"))
    private void luafy$invokeRemoveEffectCallbacks(RegistryEntry<StatusEffect> effect, CallbackInfoReturnable<Boolean> cir) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.EFFECT_LOST,
                () -> getThis().getCommandSource().withLevel(2),
                thisSo(),
                Registries.STATUS_EFFECT.getId(effect.value()).toString()
                );
    }

    @Inject(method = "eatFood", at = @At("HEAD"))
    private void luafy$invokeEatCallbacks(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.EATS,
                () -> getThis().getCommandSource().withLevel(2),

                thisSo(),
                new ItemStackScriptObject(stack)
        );
    }

    @Inject(method = "heal", at = @At("HEAD"))
    private void luafy$invokeRegenerateCallbacks(float amount, CallbackInfo ci) {
        ScriptManager.executeEventCallbacks(ScriptCallbacks.REGENERATES_HEALTH,
                () -> getThis().getCommandSource().withLevel(2),
                thisSo(),
                amount
        );
    }


}
