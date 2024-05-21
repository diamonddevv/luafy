package dev.diamond.luafy.mixin;

import dev.diamond.luafy.script.ScriptManager;
import dev.diamond.luafy.script.abstraction.obj.IScriptObject;
import dev.diamond.luafy.script.api.obj.ItemStackScriptObject;
import dev.diamond.luafy.script.api.obj.PlayerEntityScriptObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/TypedActionResult;pass(Ljava/lang/Object;)Lnet/minecraft/util/TypedActionResult;"))
    private void luafy$callbackOnUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (user instanceof ServerPlayerEntity spe) {
            ScriptManager.executeEventCallbacks(ScriptManager.CallbackEvent.USE_ITEM, spe.getCommandSource().withLevel(2), n -> {
                HashMap<String, IScriptObject> ctx = new HashMap<>();

                ctx.put("stack", new ItemStackScriptObject(spe.getStackInHand(hand)));
                ctx.put("user", new PlayerEntityScriptObject(spe));

                return ctx;
            });
        }
    }
}
