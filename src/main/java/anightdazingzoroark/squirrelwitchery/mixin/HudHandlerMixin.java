package anightdazingzoroark.squirrelwitchery.mixin;

import anightdazingzoroark.squirrelwitchery.server.items.WitchStaffItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.client.lib.events.HudHandler;

@Mixin(HudHandler.class)
public abstract class HudHandlerMixin {
    @Redirect(
            method = "renderHuds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
                    ordinal = 0
            ),
            require = 1
    )
    private Item hideWitchStaffVisOverlay(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof WitchStaffItem ? null : item;
    }
}
