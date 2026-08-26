package anightdazingzoroark.squirrelwitchery.server;

import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.blocks.SquirrelWitcheryBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;

import java.util.Arrays;

public class SquirrelWitcheryRecipes {
    private static final ResourceLocation RISUNIUM_CRYSTAL_RECIPE = new ResourceLocation(SquirrelWitchery.MODID, "crystal_cluster_risunium");

    public static void registerRecipes() {
        //---crucible stuff---
        ThaumcraftApi.addCrucibleRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "vis_crystal_risunium"),
                new CrucibleRecipe(
                    "BASEALCHEMY",
                    ThaumcraftApiHelper.makeCrystal(SquirrelWitcheryAspects.RISUNIUM),
                    "nuggetQuartz",
                    new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 2)
                ).setGroup(new ResourceLocation("thaumcraft", "viscrystalgroup"))
        );

        //---infusion stuff---
        ThaumcraftApi.addInfusionCraftingRecipe(
                RISUNIUM_CRYSTAL_RECIPE,
                new InfusionRecipe(
                        "CRYSTALFARMER",
                        new ItemStack(SquirrelWitcheryBlocks.CRYSTAL_RISUNIUM),
                        0,
                        new AspectList()
                                .add(SquirrelWitcheryAspects.RISUNIUM, 10)
                                .add(Aspect.CRYSTAL, 10)
                                .add(Aspect.TRAP, 5),
                        ThaumcraftApiHelper.makeCrystal(SquirrelWitcheryAspects.RISUNIUM),
                        new ItemStack(Items.ENDER_PEARL),
                        new ItemStack(ItemsTC.salisMundus)
                )
        );
    }

    public static void addRecipesToResearch() {
        ResearchEntry crystalFarmer = ResearchCategories.getResearch("CRYSTALFARMER");
        if (crystalFarmer == null || crystalFarmer.getStages() == null || crystalFarmer.getStages().length == 0) {
            SquirrelWitchery.LOGGER.warn("Unable to add the Risunium crystal recipe to CRYSTALFARMER research");
            return;
        }

        ResearchStage recipeStage = crystalFarmer.getStages()[crystalFarmer.getStages().length - 1];
        ResourceLocation[] recipes = recipeStage.getRecipes();
        if (recipes == null) {
            recipeStage.setRecipes(new ResourceLocation[]{RISUNIUM_CRYSTAL_RECIPE});
            return;
        }

        if (Arrays.asList(recipes).contains(RISUNIUM_CRYSTAL_RECIPE)) return;

        ResourceLocation[] recipesWithRisunium = Arrays.copyOf(recipes, recipes.length + 1);
        recipesWithRisunium[recipes.length] = RISUNIUM_CRYSTAL_RECIPE;
        recipeStage.setRecipes(recipesWithRisunium);
    }
}
