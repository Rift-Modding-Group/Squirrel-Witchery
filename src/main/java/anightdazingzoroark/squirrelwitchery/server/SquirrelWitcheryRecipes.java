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
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.items.ItemsTC;

public class SquirrelWitcheryRecipes {
    private static final ResourceLocation WITCH_COSTUME_RECIPE_GROUP = new ResourceLocation(SquirrelWitchery.MODID, "witch_costume");
    private static final ResourceLocation WITCH_STAFF_ATTACHMENT_RECIPE_GROUP = new ResourceLocation(
            SquirrelWitchery.MODID, "witch_staff_attachments"
    );

    public static void registerRecipes() {
        //---arcane workbench stuff---
        ThaumcraftApi.addArcaneCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "risunic_focal_power_booster"),
                new ShapedArcaneRecipe(
                        WITCH_STAFF_ATTACHMENT_RECIPE_GROUP,
                        SquirrelWitcheryResearch.WITCH_STAFF_ATTACHMENTS + "@2",
                        25,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 1),
                        SquirrelWitcheryItems.RISUNIC_FOCAL_POWER_BOOSTER,
                        "FG ",
                        "FP ",
                        'G', new ItemStack(Items.GLOWSTONE_DUST),
                        'P', new ItemStack(ItemsTC.plate, 1, 0),
                        'F', new ItemStack(SquirrelWitcheryItems.SQUIRREL_FUR)
                )
        );
        ThaumcraftApi.addArcaneCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "risunic_focal_lingerer"),
                new ShapedArcaneRecipe(
                        WITCH_STAFF_ATTACHMENT_RECIPE_GROUP,
                        SquirrelWitcheryResearch.WITCH_STAFF_ATTACHMENTS + "@2",
                        25,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 1),
                        SquirrelWitcheryItems.RISUNIC_FOCAL_LINGERER,
                        "FR ",
                        "FP ",
                        'R', new ItemStack(Items.REDSTONE),
                        'P', new ItemStack(ItemsTC.plate, 1, 0),
                        'F', new ItemStack(SquirrelWitcheryItems.SQUIRREL_FUR)
                )
        );
        ThaumcraftApi.addArcaneCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "risunic_focal_projectile_booster"),
                new ShapedArcaneRecipe(
                        WITCH_STAFF_ATTACHMENT_RECIPE_GROUP,
                        SquirrelWitcheryResearch.WITCH_STAFF_ATTACHMENTS + "@2",
                        25,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 1),
                        SquirrelWitcheryItems.RISUNIC_FOCAL_PROJECTILE_BOOSTER,
                        "FR ",
                        "FP ",
                        'R', new ItemStack(Items.RABBIT_FOOT),
                        'P', new ItemStack(ItemsTC.plate, 1, 0),
                        'F', new ItemStack(SquirrelWitcheryItems.SQUIRREL_FUR)
                )
        );
        ThaumcraftApi.addArcaneCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "witch_staff_attachment"),
                new WitchStaffAttachmentRecipe()
        );
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
        ThaumcraftApi.addArcaneCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "witch_staff"),
                new ShapedArcaneRecipe(
                        new ResourceLocation(SquirrelWitchery.MODID, "witch_staff"),
                        SquirrelWitcheryResearch.WITCH_STAFF + "@2",
                        100,
                        new AspectList()
                                .add(Aspect.AIR, 1)
                                .add(Aspect.EARTH, 1)
                                .add(Aspect.WATER, 1)
                                .add(Aspect.FIRE, 1)
                                .add(Aspect.ORDER, 1)
                                .add(Aspect.ENTROPY, 1),
                        SquirrelWitcheryItems.WITCH_STAFF,
                        "  F",
                        " S ",
                        "S  ",
                        'F', new ItemStack(SquirrelWitcheryItems.RISUNIC_FOCI),
                        'S', new ItemStack(Items.STICK)
                )
        );
        ThaumcraftApi.addArcaneCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "witch_shotgun"),
                new ShapedArcaneRecipe(
                        new ResourceLocation(SquirrelWitchery.MODID, "witch_shotgun"),
                        SquirrelWitcheryResearch.WITCH_SHOTGUN + "@1",
                        100,
                        new AspectList()
                                .add(SquirrelWitcheryAspects.RISUNIUM, 2)
                                .add(Aspect.FIRE, 1)
                                .add(Aspect.ENTROPY, 1),
                        SquirrelWitcheryItems.WITCH_SHOTGUN,
                        "PPP",
                        "CM ",
                        "  W",
                        'P', new ItemStack(ItemsTC.plate, 1, 0),
                        'C', ThaumcraftApiHelper.makeCrystal(SquirrelWitcheryAspects.RISUNIUM),
                        'M', new ItemStack(ItemsTC.mechanismSimple),
                        'W', new ItemStack(BlocksTC.plankGreatwood)
                )
        );
        ThaumcraftApi.addArcaneCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "nutsaber_core"),
                new ShapedArcaneRecipe(
                        new ResourceLocation(SquirrelWitchery.MODID, "nutsaber"),
                        SquirrelWitcheryResearch.NUTSABER + "@1",
                        75,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 2).add(Aspect.ORDER, 1),
                        SquirrelWitcheryItems.NUTSABER_CORE,
                        "PNP",
                        "MCM",
                        "PNP",
                        'P', new ItemStack(ItemsTC.plate, 1, 0),
                        'N', new ItemStack(SquirrelWitcheryItems.BIG_NUT),
                        'C', ThaumcraftApiHelper.makeCrystal(SquirrelWitcheryAspects.RISUNIUM),
                        'M', new ItemStack(ItemsTC.mechanismSimple)
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
        ThaumcraftApi.addCrucibleRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "risunic_foci"),
                new CrucibleRecipe(
                        SquirrelWitcheryResearch.WITCH_STAFF + "@1",
                        new ItemStack(SquirrelWitcheryItems.RISUNIC_FOCI),
                        ThaumcraftApiHelper.makeCrystal(SquirrelWitcheryAspects.RISUNIUM),
                        new AspectList()
                                .add(Aspect.CRYSTAL, 20)
                                .add(Aspect.MAGIC, 10)
                                .add(Aspect.AURA, 5)
                )
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
                new ResourceLocation(SquirrelWitchery.MODID, "witch_pendant"),
                new InfusionRecipe(
                        SquirrelWitcheryResearch.WITCH_PENDANT,
                        new ItemStack(SquirrelWitcheryItems.WITCH_PENDANT),
                        4,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 50)
                                .add(Aspect.ENERGY, 100)
                                .add(Aspect.VOID, 50),
                        new ItemStack(ItemsTC.baubles, 1, 4),
                        ThaumcraftApiHelper.makeCrystal(SquirrelWitcheryAspects.RISUNIUM),
                        new ItemStack(SquirrelWitcheryItems.SQUIRREL_FUR),
                        ThaumcraftApiHelper.makeCrystal(SquirrelWitcheryAspects.RISUNIUM),
                        new ItemStack(Items.GOLD_NUGGET)
                )
        );
        ThaumcraftApi.addInfusionCraftingRecipe(
                new ResourceLocation(SquirrelWitchery.MODID, "nutsaber"),
                new InfusionRecipe(
                        SquirrelWitcheryResearch.NUTSABER + "@2",
                        new ItemStack(SquirrelWitcheryItems.NUTSABER),
                        4,
                        new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 50)
                                .add(Aspect.ENERGY, 40)
                                .add(Aspect.AVERSION, 30)
                                .add(Aspect.MOTION, 20),
                        new ItemStack(SquirrelWitcheryItems.NUTSABER_CORE),
                        new ItemStack(ItemsTC.plate, 1, 0),
                        ThaumcraftApiHelper.makeCrystal(SquirrelWitcheryAspects.RISUNIUM),
                        "ingotThaumium",
                        new ItemStack(SquirrelWitcheryItems.SQUIRREL_FUR),
                        new ItemStack(ItemsTC.plate, 1, 0),
                        ThaumcraftApiHelper.makeCrystal(SquirrelWitcheryAspects.RISUNIUM),
                        "ingotThaumium",
                        new ItemStack(SquirrelWitcheryItems.SQUIRREL_FUR)
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
}
