package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.api.obj.entity.LivingEntityScriptObject;
import dev.diamond.luafy.script.api.obj.math.Vec3dScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.WorldScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.block.BlockStateScriptObject;
import dev.diamond.luafy.script.api.obj.minecraft.item.ItemStackScriptObject;
import dev.diamond.luafy.script.api.obj.entity.PlayerEntityScriptObject;
import dev.diamond.luafy.script.registry.callback.ScriptCallbacks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/TypedActionResult;pass(Ljava/lang/Object;)Lnet/minecraft/util/TypedActionResult;"))
    private void luafy$callbackOnUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (user instanceof ServerPlayerEntity spe) {
            ScriptManager.executeEventCallbacks(ScriptCallbacks.ON_ITEM_USED, () -> spe.getCommandSource().withLevel(2), v -> {
                v.put("stack", new ItemStackScriptObject(spe.getStackInHand(hand)));
                v.put("user", new PlayerEntityScriptObject(spe));
            });
        }
    }

    @Inject(method = "useOnBlock", at = @At("HEAD"))
    private void luafy$callbackOnUseBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (context.getPlayer() instanceof ServerPlayerEntity spe) {
            ScriptManager.executeEventCallbacks(ScriptCallbacks.ON_ITEM_USED_ON_BLOCK, () -> spe.getCommandSource().withLevel(2), v -> {
                v.put("stack", new ItemStackScriptObject(context.getStack()));
                v.put("world", new WorldScriptObject((ServerWorld) context.getWorld()));
                v.put("pos", new Vec3dScriptObject(context.getBlockPos().toCenterPos()));
                v.put("block", new BlockStateScriptObject(context.getWorld().getBlockState(context.getBlockPos())));
                v.put("user", new PlayerEntityScriptObject(spe));
            });
        }
    }

    @Inject(method = "useOnEntity", at = @At("HEAD"))
    private void luafy$callbackOnUseBlock(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (user instanceof ServerPlayerEntity spe) {
            ScriptManager.executeEventCallbacks(ScriptCallbacks.ON_ITEM_USED_ON_ENTITY, () -> spe.getCommandSource().withLevel(2), v -> {
                v.put("stack", new ItemStackScriptObject(user.getStackInHand(hand)));
                v.put("entity", new LivingEntityScriptObject(entity));
                v.put("user", new PlayerEntityScriptObject(spe));
            });
        }
    }
}
