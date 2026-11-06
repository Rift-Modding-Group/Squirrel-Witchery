package anightdazingzoroark.squirrelwitchery;

import net.minecraftforge.common.config.Config;

@Config(modid = SquirrelWitchery.MODID)
public class SquirrelWitcheryConfig {
    @Config.Name("Nut Drop Rate")
    @Config.Comment({
            "1/x chance for which a nut will drop by breaking leaves.",
            "Setting to 0 disables nuts dropping from leaves entirely."
    })
    public static int nutDropRate = 16;

    @Config.Name("Nut Drop Quantity Range")
    @Config.Comment("Upper and lower limit for amount of nuts that can drop from breaking leaves.")
    public static int[] nutDropQuantity = new int[]{2, 3};

    @Config.Name("Big Nut Drop Rate")
    @Config.Comment({
            "1/x chance for which a big nut will drop by breaking leaves.",
            "Setting to 0 disables big nuts dropping from leaves entirely."
    })
    public static int bigNutDropRate = 32;

    @Config.Name("Big Nut Drop Quantity Range")
    @Config.Comment("Upper and lower limit for amount of nuts that can drop from breaking leaves.")
    public static int[] bigNutDropQuantity = new int[]{1, 1};

    @Config.Name("Magical Squirrel Behavior")
    @Config.Comment("Behavior specific to tamed magical squirrels.")
    public static final MagicalSquirrelBehavior magicalSquirrelBehavior = new MagicalSquirrelBehavior();

    public static class MagicalSquirrelBehavior {
        @Config.Name("Magical Squirrel Usable Melee Weapons")
        @Config.Comment("Items that a Magical Squirrel can hold and use as melee weapons.")
        public String[] usableMeleeWeapons = {
                "minecraft:wooden_sword:-1",
                "minecraft:stone_sword:-1",
                "minecraft:iron_sword:-1",
                "minecraft:golden_sword:-1",
                "minecraft:diamond_sword:-1",
                "thaumcraft:thaumium_sword:-1",
                "thaumcraft:void_sword:-1",
                "thaumcraft:elemental_sword:-1",
                "squirrelwitchery:squirrel_tail_hammer:-1",
                "squirrelwitchery:nutsaber"
        };

        @Config.Name("Magical Squirrel Can Wear Armor")
        @Config.Comment("Allows Magical Squirrels to wear armor placed in their inventory")
        public boolean canWearArmor = true;

        @Config.Name("Magical Squirrel Can Use Bows")
        @Config.Comment({
                "Allows Magical Squirrels to use bows.",
                "This includes any other modded item that extends ItemBow."
        })
        public boolean canUseBows = true;
    }
}