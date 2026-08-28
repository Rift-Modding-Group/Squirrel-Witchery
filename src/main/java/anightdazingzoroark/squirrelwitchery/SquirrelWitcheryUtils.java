package anightdazingzoroark.squirrelwitchery;

import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.resources.ItemCrystalEssence;

public class SquirrelWitcheryUtils {
    public static boolean isRisuniumCrystal(@NotNull ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemCrystalEssence crystal)) return false;

        AspectList aspects = crystal.getAspects(stack);
        return aspects != null && aspects.getAmount(SquirrelWitcheryAspects.RISUNIUM) > 0;
    }
}
