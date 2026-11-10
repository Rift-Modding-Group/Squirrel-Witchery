package anightdazingzoroark.squirrelwitchery;

import anightdazingzoroark.squirrelwitchery.compat.PamsHarvestcraftCompat;
import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.resources.ItemCrystalEssence;

import java.util.UUID;

public class SquirrelWitcheryUtils {
    public static boolean isRisuniumCrystal(@NotNull ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemCrystalEssence crystal)) return false;

        AspectList aspects = crystal.getAspects(stack);
        return aspects != null && aspects.getAmount(SquirrelWitcheryAspects.RISUNIUM) > 0;
    }

    public static boolean isValidNut(@NotNull ItemStack stack) {
        return stack.getItem() == SquirrelWitcheryItems.NUT || stack.getItem() == SquirrelWitcheryItems.BIG_NUT
                || (Loader.isModLoaded("harvestcraft") && PamsHarvestcraftCompat.isPamsHarvestcraftNut(stack));
    }

    @Nullable
    public static Entity getEntityWithUUID(@NotNull World world, @NotNull UUID uuid) {
        for (Entity entity : world.loadedEntityList) {
            if (entity == null) continue;
            if (entity.getUniqueID().equals(uuid)) return entity;
        }
        return null;
    }

    /**
     * the good ol reliable
     * */
    public static boolean itemStackMatchesString(@NotNull ItemStack itemStack, @NotNull String string) {
        ResourceLocation itemResource = Item.REGISTRY.getNameForObject(itemStack.getItem());
        if (itemResource == null) return false;

        ImmutablePair<String, Integer> stackNameAndMeta = getRegistryNameAndMeta(string);
        String testedStackName = itemResource.toString();

        //if metadata is negative, presume wildcard
        if (stackNameAndMeta.getRight() < 0) return testedStackName.equals(stackNameAndMeta.getLeft());
        else return testedStackName.equals(stackNameAndMeta.getLeft()) && itemStack.getMetadata() == stackNameAndMeta.getRight();
    }

    @NotNull
    private static ImmutablePair<String, Integer> getRegistryNameAndMeta(@NotNull String string) {
        int firstColonIndex = string.indexOf(":");
        int secondColonIndex = string.indexOf(":", firstColonIndex + 1);

        //no metadata, presume default of 0
        if (secondColonIndex < 0) return new ImmutablePair<>(string, 0);

        String itemId = string.substring(0, secondColonIndex);
        int metadata = Integer.parseInt(string.substring(secondColonIndex + 1));

        return new ImmutablePair<>(itemId, metadata);
    }
}
