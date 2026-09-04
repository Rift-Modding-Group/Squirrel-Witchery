package anightdazingzoroark.squirrelwitchery.server;

import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.blocks.SquirrelWitcheryBlocks;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;

public class SquirrelWitcheryRecipes {
    private static final ResourceLocation WITCH_COSTUME_RECIPE_GROUP = new ResourceLocation(SquirrelWitchery.MODID, "witch_costume");

    public static void registerRecipes() {
        //---arcane workbench stuff---
        ThaumcraftApi.addArcaneCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "witch_hat"),
                new ShapedArcaneRecipe(
                        WITCH_COSTUME_RECIPE_GROUP,
                        SquirrelWitcheryResearch.WITCH_COSTUME + "@1",
                        100,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 3),
                        SquirrelWitcheryItems.WITCH_HAT,
                        "  F",
                        " F ",
                        "FFF",
                        'F', new ItemStack(SquirrelWitcheryItems.SQUIRREL_FUR)
                )
        );
        ThaumcraftApi.addArcaneCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "witch_robe"),
                new ShapedArcaneRecipe(
                        WITCH_COSTUME_RECIPE_GROUP,
                        SquirrelWitcheryResearch.WITCH_COSTUME + "@1",
                        100,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 3),
                        SquirrelWitcheryItems.WITCH_ROBE,
                        "F F",
                        "FFF",
                        "FFF",
                        'F', new ItemStack(SquirrelWitcheryItems.SQUIRREL_FUR)
                )
        );
        ThaumcraftApi.addArcaneCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "witch_skirt"),
                new ShapedArcaneRecipe(
                        WITCH_COSTUME_RECIPE_GROUP,
                        SquirrelWitcheryResearch.WITCH_COSTUME + "@1",
                        100,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 3),
                        SquirrelWitcheryItems.WITCH_SKIRT,
                        "FFF",
                        "F F",
                        "F F",
                        'F', new ItemStack(SquirrelWitcheryItems.SQUIRREL_FUR)
                )
        );
        ThaumcraftApi.addArcaneCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "witch_boots"),
                new ShapedArcaneRecipe(
                        WITCH_COSTUME_RECIPE_GROUP,
                        SquirrelWitcheryResearch.WITCH_COSTUME + "@1",
                        100,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 3),
                        SquirrelWitcheryItems.WITCH_BOOTS,
                        "F F",
                        "F F",
                        'F', new ItemStack(SquirrelWitcheryItems.SQUIRREL_FUR)
                )
        );
        ThaumcraftApi.addArcaneCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "witch_broom"),
                new ShapedArcaneRecipe(
                        new ResourceLocation(SquirrelWitchery.MODID, "witch_broom"),
                        SquirrelWitcheryResearch.WITCH_BROOM + "@1",
                        50,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 1).add(Aspect.AIR, 1),
                        SquirrelWitcheryItems.WITCH_BROOM,
                        "  S",
                        "FS ",
                        "FF ",
                        'F', new ItemStack(SquirrelWitcheryItems.SQUIRREL_FUR),
                        'S', new ItemStack(Items.STICK)
                )
        );

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
                new ResourceLocation(SquirrelWitchery.MODID, "crystal_cluster_risunium"),
                new InfusionRecipe(
                        "CRYSTALFARMER",
                        new ItemStack(SquirrelWitcheryBlocks.CRYSTAL_RISUNIUM),
                        0,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 10)
                                .add(Aspect.CRYSTAL, 10)
                                .add(Aspect.TRAP, 5),
                        ThaumcraftApiHelper.makeCrystal(SquirrelWitcheryAspects.RISUNIUM),
                        new ItemStack(Items.WHEAT_SEEDS),
                        new ItemStack(ItemsTC.salisMundus)
                )
        );
        ThaumcraftApi.addInfusionCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "dark_witch_hat"),
                new InfusionRecipe(
                        SquirrelWitcheryResearch.DARK_WITCH_COSTUME,
                        new ItemStack(SquirrelWitcheryItems.DARK_WITCH_HAT),
                        6,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 25)
                                .add(Aspect.METAL, 25)
                                .add(Aspect.SENSES, 25)
                                .add(Aspect.PROTECT, 25)
                                .add(Aspect.ENERGY, 25)
                                .add(Aspect.ELDRITCH, 25)
                                .add(Aspect.VOID, 25),
                        new ItemStack(SquirrelWitcheryItems.WITCH_HAT),
                        new ItemStack(ItemsTC.voidHelm),
                        new ItemStack(ItemsTC.fabric),
                        new ItemStack(ItemsTC.fabric),
                        new ItemStack(ItemsTC.salisMundus),
                        new ItemStack(ItemsTC.fabric),
                        new ItemStack(ItemsTC.fabric)
                )
        );
        ThaumcraftApi.addInfusionCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "dark_witch_robe"),
                new InfusionRecipe(
                        SquirrelWitcheryResearch.DARK_WITCH_COSTUME,
                        new ItemStack(SquirrelWitcheryItems.DARK_WITCH_ROBE),
                        6,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 35)
                                .add(Aspect.METAL, 35)
                                .add(Aspect.PROTECT, 35)
                                .add(Aspect.ENERGY, 25)
                                .add(Aspect.ELDRITCH, 25)
                                .add(Aspect.VOID, 35),
                        new ItemStack(SquirrelWitcheryItems.WITCH_ROBE),
                        new ItemStack(ItemsTC.voidChest),
                        "plateVoid",
                        "plateVoid",
                        new ItemStack(ItemsTC.salisMundus),
                        new ItemStack(ItemsTC.fabric),
                        "leather"
                )
        );
        ThaumcraftApi.addInfusionCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "dark_witch_skirt"),
                new InfusionRecipe(
                        SquirrelWitcheryResearch.DARK_WITCH_COSTUME,
                        new ItemStack(SquirrelWitcheryItems.DARK_WITCH_SKIRT),
                        6,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 30)
                                .add(Aspect.METAL, 30)
                                .add(Aspect.PROTECT, 30)
                                .add(Aspect.ENERGY, 25)
                                .add(Aspect.ELDRITCH, 25)
                                .add(Aspect.VOID, 30),
                        new ItemStack(SquirrelWitcheryItems.WITCH_SKIRT),
                        new ItemStack(ItemsTC.voidLegs),
                        "plateVoid",
                        "plateVoid",
                        new ItemStack(ItemsTC.salisMundus),
                        new ItemStack(ItemsTC.fabric),
                        "leather"
                )
        );
        ThaumcraftApi.addInfusionCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "dark_witch_boots"),
                new InfusionRecipe(
                        SquirrelWitcheryResearch.DARK_WITCH_COSTUME,
                        new ItemStack(SquirrelWitcheryItems.DARK_WITCH_BOOTS),
                        6,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 20)
                                .add(Aspect.METAL, 20)
                                .add(Aspect.PROTECT, 20)
                                .add(Aspect.ENERGY, 25)
                                .add(Aspect.ELDRITCH, 25)
                                .add(Aspect.VOID, 20),
                        new ItemStack(SquirrelWitcheryItems.WITCH_BOOTS),
                        new ItemStack(ItemsTC.voidBoots),
                        "plateVoid",
                        "plateVoid",
                        new ItemStack(ItemsTC.salisMundus),
                        new ItemStack(ItemsTC.fabric),
                        "leather"
                )
        );
    }

    public static void configureResearch() {
        //manually set Risunium vis crystal research icons here, doesn't work in json
        ResearchEntry visCrystalRisunium = ResearchCategories.getResearch(SquirrelWitcheryResearch.VIS_CRYSTAL_RISUNIUM);
        if (visCrystalRisunium == null) {
            SquirrelWitchery.LOGGER.warn("Unable to set the Risunium vis crystal research icon");
        }
        else {
            visCrystalRisunium.setIcons(new Object[]{ThaumcraftApiHelper.makeCrystal(SquirrelWitcheryAspects.RISUNIUM)});
        }
    }
}
