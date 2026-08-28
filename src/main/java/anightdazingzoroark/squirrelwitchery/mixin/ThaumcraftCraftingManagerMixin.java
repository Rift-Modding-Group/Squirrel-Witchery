package anightdazingzoroark.squirrelwitchery.mixin;

import anightdazingzoroark.squirrelwitchery.SquirrelWitcheryUtils;
import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

@Mixin(value = ThaumcraftCraftingManager.class, remap = false)
public abstract class ThaumcraftCraftingManagerMixin {
    /**
     * make sure that in the arcane workbench risunium vis crystal recipes are prioritized
     * when said crystal is put in a slot
     * */
    @Inject(method = "findMatchingArcaneRecipe", at = @At("HEAD"), cancellable = true, require = 1)
    private static void prioritizeRisuniumRecipes(InventoryCrafting inventory, EntityPlayer player, CallbackInfoReturnable<IArcaneRecipe> callback) {
        boolean containsRisunium = false;
        for (int slot = 9; slot < 15; slot++) {
            if (SquirrelWitcheryUtils.isRisuniumCrystal(inventory.getStackInSlot(slot))) {
                containsRisunium = true;
                break;
            }
        }
        if (!containsRisunium) return;

        for (ResourceLocation recipeKey : CraftingManager.REGISTRY.getKeys()) {
            IRecipe recipe = CraftingManager.REGISTRY.getObject(recipeKey);
            if (!(recipe instanceof IArcaneRecipe arcaneRecipe)) continue;

            AspectList requiredCrystals = arcaneRecipe.getCrystals();
            if (requiredCrystals != null && requiredCrystals.getAmount(SquirrelWitcheryAspects.RISUNIUM) > 0
                    && recipe.matches(inventory, player.world)
            ) {
                callback.setReturnValue(arcaneRecipe);
                return;
            }
        }
    }
}
