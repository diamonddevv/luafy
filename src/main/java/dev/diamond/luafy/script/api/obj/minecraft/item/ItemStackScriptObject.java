package dev.diamond.luafy.script.api.obj.minecraft.item;

import dev.diamond.luafy.script.abstraction.NamedParam;
import dev.diamond.luafy.script.abstraction.obj.AbstractTypedScriptObject;
import dev.diamond.luafy.script.api.obj.entity.PlayerEntityScriptObject;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;

public class ItemStackScriptObject extends AbstractTypedScriptObject<ItemStack> {
    private final ItemStack stack;

    public ItemStackScriptObject(ItemStack stack) {
        this.stack = stack;
    }



    public ItemStack get() {
        return stack;
    }

    @Override
    public void getTypedFunctions(TypedFunctionList f) {
        f.add_NoParams_Desc("get_id", args -> Registries.ITEM.getId(stack.getItem()).toString(), "Gets the registry of the item this stack contains.", String.class);
        f.add_NoParams_Desc("get_count", args -> stack.getCount(), "Gets the size of this stack.", Number.class);

        f.add_NoParams_Desc("get_components", args -> new ComponentMapScriptObject(stack.getComponents()), "Gets the components of this stack", ComponentMapScriptObject.class);

        f.add_Void_Desc("set_cooldown_ticks", args -> {
            ServerPlayerEntity player = args[0].asScriptObjectAssertive(PlayerEntityScriptObject.class).player;
            int ticks = args[1].asInt();
            player.getItemCooldownManager().set(stack.getItem(), ticks);
            return null;
        }, "Sets the cooldown timer of this item for the player.", new NamedParam("player", PlayerEntityScriptObject.class), new NamedParam("ticks", Number.class));

        f.add_Desc("get_cooldown_ticks", args -> {
            ServerPlayerEntity player = args[0].asScriptObjectAssertive(PlayerEntityScriptObject.class).player;
            return player.getItemCooldownManager().getCooldownProgress(stack.getItem(), 0f);
        },"Gets the remaining cooldown ticks for this item for the player.", Number.class, new NamedParam("player", PlayerEntityScriptObject.class));

        f.add_Void_Desc("remove_cooldown", args -> {
            ServerPlayerEntity player = args[0].asScriptObjectAssertive(PlayerEntityScriptObject.class).player;
            player.getItemCooldownManager().remove(stack.getItem());
            return null;
        }, "Removes the cooldown of this item for the player.", new NamedParam("player", PlayerEntityScriptObject.class));
    }
}
