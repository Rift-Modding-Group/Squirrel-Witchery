package anightdazingzoroark.squirrelwitchery.compat;

import com.pam.harvestcraft.blocks.CropRegistry;
import com.pam.harvestcraft.blocks.FruitRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PamsHarvestcraftCompat {
    public static boolean isPamsHarvestcraftNut(@NotNull ItemStack stack) {
        return stack.getItem() == CropRegistry.getFood(CropRegistry.PEANUT)
                || stack.getItem() == CropRegistry.getFood(CropRegistry.WATERCHESTNUT)
                || stack.getItem() == FruitRegistry.getFood(FruitRegistry.HAZELNUT)
                || stack.getItem() == FruitRegistry.getFood(FruitRegistry.COCONUT)
                || stack.getItem() == FruitRegistry.getFood(FruitRegistry.CHESTNUT)
                || stack.getItem() == FruitRegistry.getFood(FruitRegistry.WALNUT)
                || stack.getItem() == FruitRegistry.getFood(FruitRegistry.NUTMEG);
    }
}
