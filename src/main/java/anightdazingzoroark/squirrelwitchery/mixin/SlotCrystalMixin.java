package anightdazingzoroark.squirrelwitchery.mixin;

import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.container.slot.SlotCrystal;

@Mixin(value = SlotCrystal.class, remap = false)
public abstract class SlotCrystalMixin {
    @Inject(method = "isValidCrystal", at = @At("HEAD"), cancellable = true, require = 1)
    private static void acceptRisuniumInEveryCrystalSlot(ItemStack stack, Aspect aspect, CallbackInfoReturnable<Boolean> callback) {
        ItemStack risuniumCrystal = ThaumcraftApiHelper.makeCrystal(SquirrelWitcheryAspects.RISUNIUM);
        if (stack.getItem() == risuniumCrystal.getItem() && ItemStack.areItemStackTagsEqual(stack, risuniumCrystal)) {
            callback.setReturnValue(true);
        }
    }
}
