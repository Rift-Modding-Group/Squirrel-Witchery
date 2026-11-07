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
                "squirrelwitchery:nutsaber",

                "spartanweaponry:dagger_wood:-1",
                "spartanweaponry:dagger_stone:-1",
                "spartanweaponry:dagger_iron:-1",
                "spartanweaponry:dagger_gold:-1",
                "spartanweaponry:dagger_diamond:-1",
                "spartanweaponry:dagger_copper:-1",
                "spartanweaponry:dagger_tin:-1",
                "spartanweaponry:dagger_bronze:-1",
                "spartanweaponry:dagger_steel:-1",
                "spartanweaponry:dagger_silver:-1",
                "spartanweaponry:dagger_invar:-1",
                "spartanweaponry:dagger_platinum:-1",
                "spartanweaponry:dagger_electrum:-1",
                "spartanweaponry:dagger_nickel:-1",
                "spartanweaponry:dagger_lead:-1",

                "spartanweaponry:parrying_dagger_wood:-1",
                "spartanweaponry:parrying_dagger_stone:-1",
                "spartanweaponry:parrying_dagger_iron:-1",
                "spartanweaponry:parrying_dagger_gold:-1",
                "spartanweaponry:parrying_dagger_diamond:-1",
                "spartanweaponry:parrying_dagger_copper:-1",
                "spartanweaponry:parrying_dagger_tin:-1",
                "spartanweaponry:parrying_dagger_bronze:-1",
                "spartanweaponry:parrying_dagger_steel:-1",
                "spartanweaponry:parrying_dagger_silver:-1",
                "spartanweaponry:parrying_dagger_invar:-1",
                "spartanweaponry:parrying_dagger_platinum:-1",
                "spartanweaponry:parrying_dagger_electrum:-1",
                "spartanweaponry:parrying_dagger_nickel:-1",
                "spartanweaponry:parrying_dagger_lead:-1",

                "spartanweaponry:longsword_wood:-1",
                "spartanweaponry:longsword_stone:-1",
                "spartanweaponry:longsword_iron:-1",
                "spartanweaponry:longsword_gold:-1",
                "spartanweaponry:longsword_diamond:-1",
                "spartanweaponry:longsword_copper:-1",
                "spartanweaponry:longsword_tin:-1",
                "spartanweaponry:longsword_bronze:-1",
                "spartanweaponry:longsword_steel:-1",
                "spartanweaponry:longsword_silver:-1",
                "spartanweaponry:longsword_invar:-1",
                "spartanweaponry:longsword_platinum:-1",
                "spartanweaponry:longsword_electrum:-1",
                "spartanweaponry:longsword_nickel:-1",
                "spartanweaponry:longsword_lead:-1",

                "spartanweaponry:katana_wood:-1",
                "spartanweaponry:katana_stone:-1",
                "spartanweaponry:katana_iron:-1",
                "spartanweaponry:katana_gold:-1",
                "spartanweaponry:katana_diamond:-1",
                "spartanweaponry:katana_copper:-1",
                "spartanweaponry:katana_tin:-1",
                "spartanweaponry:katana_bronze:-1",
                "spartanweaponry:katana_steel:-1",
                "spartanweaponry:katana_silver:-1",
                "spartanweaponry:katana_invar:-1",
                "spartanweaponry:katana_platinum:-1",
                "spartanweaponry:katana_electrum:-1",
                "spartanweaponry:katana_nickel:-1",
                "spartanweaponry:katana_lead:-1",

                "spartanweaponry:scythe_wood:-1",
                "spartanweaponry:scythe_stone:-1",
                "spartanweaponry:scythe_iron:-1",
                "spartanweaponry:scythe_gold:-1",
                "spartanweaponry:scythe_diamond:-1",
                "spartanweaponry:scythe_copper:-1",
                "spartanweaponry:scythe_tin:-1",
                "spartanweaponry:scythe_bronze:-1",
                "spartanweaponry:scythe_steel:-1",
                "spartanweaponry:scythe_silver:-1",
                "spartanweaponry:scythe_invar:-1",
                "spartanweaponry:scythe_platinum:-1",
                "spartanweaponry:scythe_electrum:-1",
                "spartanweaponry:scythe_nickel:-1",
                "spartanweaponry:scythe_lead:-1",

                "spartanweaponry:saber_wood:-1",
                "spartanweaponry:saber_stone:-1",
                "spartanweaponry:saber_iron:-1",
                "spartanweaponry:saber_gold:-1",
                "spartanweaponry:saber_diamond:-1",
                "spartanweaponry:saber_copper:-1",
                "spartanweaponry:saber_tin:-1",
                "spartanweaponry:saber_bronze:-1",
                "spartanweaponry:saber_steel:-1",
                "spartanweaponry:saber_silver:-1",
                "spartanweaponry:saber_invar:-1",
                "spartanweaponry:saber_platinum:-1",
                "spartanweaponry:saber_electrum:-1",
                "spartanweaponry:saber_nickel:-1",
                "spartanweaponry:saber_lead:-1",

                "spartanweaponry:rapier_wood:-1",
                "spartanweaponry:rapier_stone:-1",
                "spartanweaponry:rapier_iron:-1",
                "spartanweaponry:rapier_gold:-1",
                "spartanweaponry:rapier_diamond:-1",
                "spartanweaponry:rapier_copper:-1",
                "spartanweaponry:rapier_tin:-1",
                "spartanweaponry:rapier_bronze:-1",
                "spartanweaponry:rapier_steel:-1",
                "spartanweaponry:rapier_silver:-1",
                "spartanweaponry:rapier_invar:-1",
                "spartanweaponry:rapier_platinum:-1",
                "spartanweaponry:rapier_electrum:-1",
                "spartanweaponry:rapier_nickel:-1",
                "spartanweaponry:rapier_lead:-1",

                "spartanweaponry:greatsword_wood:-1",
                "spartanweaponry:greatsword_stone:-1",
                "spartanweaponry:greatsword_iron:-1",
                "spartanweaponry:greatsword_gold:-1",
                "spartanweaponry:greatsword_diamond:-1",
                "spartanweaponry:greatsword_copper:-1",
                "spartanweaponry:greatsword_tin:-1",
                "spartanweaponry:greatsword_bronze:-1",
                "spartanweaponry:greatsword_steel:-1",
                "spartanweaponry:greatsword_silver:-1",
                "spartanweaponry:greatsword_invar:-1",
                "spartanweaponry:greatsword_platinum:-1",
                "spartanweaponry:greatsword_electrum:-1",
                "spartanweaponry:greatsword_nickel:-1",
                "spartanweaponry:greatsword_lead:-1",

                "spartanweaponry:caestus:-1",
                "spartanweaponry:caestus_studded:-1",

                "spartanweaponry:club_wood:-1",
                "spartanweaponry:club_studded:-1",

                "spartanweaponry:hammer_wood:-1",
                "spartanweaponry:hammer_stone:-1",
                "spartanweaponry:hammer_iron:-1",
                "spartanweaponry:hammer_gold:-1",
                "spartanweaponry:hammer_diamond:-1",
                "spartanweaponry:hammer_copper:-1",
                "spartanweaponry:hammer_tin:-1",
                "spartanweaponry:hammer_bronze:-1",
                "spartanweaponry:hammer_steel:-1",
                "spartanweaponry:hammer_silver:-1",
                "spartanweaponry:hammer_invar:-1",
                "spartanweaponry:hammer_platinum:-1",
                "spartanweaponry:hammer_electrum:-1",
                "spartanweaponry:hammer_nickel:-1",
                "spartanweaponry:hammer_lead:-1",

                "spartanweaponry:warhammer_wood:-1",
                "spartanweaponry:warhammer_stone:-1",
                "spartanweaponry:warhammer_iron:-1",
                "spartanweaponry:warhammer_gold:-1",
                "spartanweaponry:warhammer_diamond:-1",
                "spartanweaponry:warhammer_copper:-1",
                "spartanweaponry:warhammer_tin:-1",
                "spartanweaponry:warhammer_bronze:-1",
                "spartanweaponry:warhammer_steel:-1",
                "spartanweaponry:warhammer_silver:-1",
                "spartanweaponry:warhammer_invar:-1",
                "spartanweaponry:warhammer_platinum:-1",
                "spartanweaponry:warhammer_electrum:-1",
                "spartanweaponry:warhammer_nickel:-1",
                "spartanweaponry:warhammer_lead:-1",

                "spartanweaponry:spear_wood:-1",
                "spartanweaponry:spear_stone:-1",
                "spartanweaponry:spear_iron:-1",
                "spartanweaponry:spear_gold:-1",
                "spartanweaponry:spear_diamond:-1",
                "spartanweaponry:spear_copper:-1",
                "spartanweaponry:spear_tin:-1",
                "spartanweaponry:spear_bronze:-1",
                "spartanweaponry:spear_steel:-1",
                "spartanweaponry:spear_silver:-1",
                "spartanweaponry:spear_invar:-1",
                "spartanweaponry:spear_platinum:-1",
                "spartanweaponry:spear_electrum:-1",
                "spartanweaponry:spear_nickel:-1",
                "spartanweaponry:spear_lead:-1",

                "spartanweaponry:halberd_wood:-1",
                "spartanweaponry:halberd_stone:-1",
                "spartanweaponry:halberd_iron:-1",
                "spartanweaponry:halberd_gold:-1",
                "spartanweaponry:halberd_diamond:-1",
                "spartanweaponry:halberd_copper:-1",
                "spartanweaponry:halberd_tin:-1",
                "spartanweaponry:halberd_bronze:-1",
                "spartanweaponry:halberd_steel:-1",
                "spartanweaponry:halberd_silver:-1",
                "spartanweaponry:halberd_invar:-1",
                "spartanweaponry:halberd_platinum:-1",
                "spartanweaponry:halberd_electrum:-1",
                "spartanweaponry:halberd_nickel:-1",
                "spartanweaponry:halberd_lead:-1",

                "spartanweaponry:pike_wood:-1",
                "spartanweaponry:pike_stone:-1",
                "spartanweaponry:pike_iron:-1",
                "spartanweaponry:pike_gold:-1",
                "spartanweaponry:pike_diamond:-1",
                "spartanweaponry:pike_copper:-1",
                "spartanweaponry:pike_tin:-1",
                "spartanweaponry:pike_bronze:-1",
                "spartanweaponry:pike_steel:-1",
                "spartanweaponry:pike_silver:-1",
                "spartanweaponry:pike_invar:-1",
                "spartanweaponry:pike_platinum:-1",
                "spartanweaponry:pike_electrum:-1",
                "spartanweaponry:pike_nickel:-1",
                "spartanweaponry:pike_lead:-1",

                "spartanweaponry:lance_wood:-1",
                "spartanweaponry:lance_stone:-1",
                "spartanweaponry:lance_iron:-1",
                "spartanweaponry:lance_gold:-1",
                "spartanweaponry:lance_diamond:-1",
                "spartanweaponry:lance_copper:-1",
                "spartanweaponry:lance_tin:-1",
                "spartanweaponry:lance_bronze:-1",
                "spartanweaponry:lance_steel:-1",
                "spartanweaponry:lance_silver:-1",
                "spartanweaponry:lance_invar:-1",
                "spartanweaponry:lance_platinum:-1",
                "spartanweaponry:lance_electrum:-1",
                "spartanweaponry:lance_nickel:-1",
                "spartanweaponry:lance_lead:-1",

                "spartanweaponry:battleaxe_wood:-1",
                "spartanweaponry:battleaxe_stone:-1",
                "spartanweaponry:battleaxe_iron:-1",
                "spartanweaponry:battleaxe_gold:-1",
                "spartanweaponry:battleaxe_diamond:-1",
                "spartanweaponry:battleaxe_copper:-1",
                "spartanweaponry:battleaxe_tin:-1",
                "spartanweaponry:battleaxe_bronze:-1",
                "spartanweaponry:battleaxe_steel:-1",
                "spartanweaponry:battleaxe_silver:-1",
                "spartanweaponry:battleaxe_invar:-1",
                "spartanweaponry:battleaxe_platinum:-1",
                "spartanweaponry:battleaxe_electrum:-1",
                "spartanweaponry:battleaxe_nickel:-1",
                "spartanweaponry:battleaxe_lead:-1",

                "spartanweaponry:mace_wood:-1",
                "spartanweaponry:mace_stone:-1",
                "spartanweaponry:mace_iron:-1",
                "spartanweaponry:mace_gold:-1",
                "spartanweaponry:mace_diamond:-1",
                "spartanweaponry:mace_copper:-1",
                "spartanweaponry:mace_tin:-1",
                "spartanweaponry:mace_bronze:-1",
                "spartanweaponry:mace_steel:-1",
                "spartanweaponry:mace_silver:-1",
                "spartanweaponry:mace_invar:-1",
                "spartanweaponry:mace_platinum:-1",
                "spartanweaponry:mace_electrum:-1",
                "spartanweaponry:mace_nickel:-1",
                "spartanweaponry:mace_lead:-1",

                "spartanweaponry:glaive_wood:-1",
                "spartanweaponry:glaive_stone:-1",
                "spartanweaponry:glaive_iron:-1",
                "spartanweaponry:glaive_gold:-1",
                "spartanweaponry:glaive_diamond:-1",
                "spartanweaponry:glaive_copper:-1",
                "spartanweaponry:glaive_tin:-1",
                "spartanweaponry:glaive_bronze:-1",
                "spartanweaponry:glaive_steel:-1",
                "spartanweaponry:glaive_silver:-1",
                "spartanweaponry:glaive_invar:-1",
                "spartanweaponry:glaive_platinum:-1",
                "spartanweaponry:glaive_electrum:-1",
                "spartanweaponry:glaive_nickel:-1",
                "spartanweaponry:glaive_lead:-1",

                "spartanweaponry:staff:-1",
                "spartanweaponry:staff_stone:-1",
                "spartanweaponry:staff_iron:-1",
                "spartanweaponry:staff_gold:-1",
                "spartanweaponry:staff_diamond:-1",
                "spartanweaponry:staff_copper:-1",
                "spartanweaponry:staff_tin:-1",
                "spartanweaponry:staff_bronze:-1",
                "spartanweaponry:staff_steel:-1",
                "spartanweaponry:staff_silver:-1",
                "spartanweaponry:staff_invar:-1",
                "spartanweaponry:staff_platinum:-1",
                "spartanweaponry:staff_electrum:-1",
                "spartanweaponry:staff_nickel:-1",
                "spartanweaponry:staff_lead:-1",

                "tconstruct:scythe:-1",
                "tconstruct:broadsword:-1",
                "tconstruct:longsword:-1",
                "tconstruct:rapier:-1",
                "tconstruct:frypan:-1",
                "tconstruct:battlesign:-1",
                "tconstruct:cleaver:-1"
        };

        @Config.Name("Magical Squirrel Can Wear Armor")
        @Config.Comment({
                "Allows Magical Squirrels to wear armor placed in their inventory",
                "Should also most support moded armors."
        })
        public boolean canWearArmor = true;

        @Config.Name("Magical Squirrel Can Use Bows")
        @Config.Comment({
                "Allows Magical Squirrels to use bows.",
                "This includes any other modded item that extends ItemBow."
        })
        public boolean canUseBows = true;

        @Config.Name("Magical Squirrel Can Use Witch's Shotgun")
        @Config.Comment({
                "Allows Magical Squirrels to use the Witch's Shotgun."
        })
        public boolean canUseWitchsShotgun = true;

        @Config.Name("Magical Squirrel Can Use Modded Ranged Weapons")
        @Config.Comment({
                "Allows Magical Squirrels to use modded ranged weapons.",
                "Supports Spartan Weaponry and Tinkers' Construct ranged weapons."
        })
        public boolean canUseModdedRangedWeapons = true;

        @Config.Name("Magical Squirrel Can Use Spell Casting Items")
        @Config.Comment({
                "Allows Magical Squirrels to use spell casting items.",
                "Basically includes anything that extends ItemCaster",
                "Like the Caster's Gauntlet and the Witch's Staff.",
                "Uses the installed focus, or installs the last focus from the Magical Squirrel's inventory if none is installed."
        })
        public boolean canUseCastingItems = true;

        @Config.Name("Magical Squirrel Owner Healing Health Threshold")
        @Config.Comment({
                "The Health Focus effect is a special case, as it will only be used by Magical Squirrels",
                "to attack undead mobs or heal its owner. This defines the threshold at which Magical Squirrels",
                "with access to this spell will start using it on their owner, then continue until their health is full."
        })
        @Config.RangeDouble(min = 0D, max = 100D)
        public double ownerHealingHealthThreshold = 25D;
    }
}
