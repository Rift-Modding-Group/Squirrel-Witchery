package anightdazingzoroark.squirrelwitchery.server.items;

import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.ThaumcraftMaterials;

import java.util.ArrayList;
import java.util.List;

public class SquirrelWitcheryItems {
    public static final List<Item> ITEMS = new ArrayList<>();

    public static Item NUT;
    public static Item BIG_NUT;
    public static Item SQUIRREL_FUR;
    public static Item CRYSTALLIZED_SQUIRREL_HEART;
    public static ItemArmor WITCH_HAT;
    public static ItemArmor WITCH_ROBE;
    public static ItemArmor WITCH_SKIRT;
    public static ItemArmor WITCH_BOOTS;
    public static ItemArmor DARK_WITCH_HAT;
    public static ItemArmor DARK_WITCH_ROBE;
    public static ItemArmor DARK_WITCH_SKIRT;
    public static ItemArmor DARK_WITCH_BOOTS;
    public static WitchBroomItem WITCH_BROOM;
    public static WitchStaffItem WITCH_STAFF;
    public static Item RISUNIC_FOCI;
    public static Item RISUNIC_FOCAL_POWER_BOOSTER;
    public static Item RISUNIC_FOCAL_LINGERER;
    public static Item RISUNIC_FOCAL_PROJECTILE_BOOSTER;
    public static WitchShotgunItem WITCH_SHOTGUN;
    /*
    public static Item NUT_BOMB;
    public static Item NUTSABER;
     */

    public static void registerItems() {
        NUT = registerItem(new Item(), "nut", true);
        BIG_NUT = registerItem(new Item(), "big_nut", true);
        SQUIRREL_FUR = registerItem(new Item(), "squirrel_fur", true);
        CRYSTALLIZED_SQUIRREL_HEART = registerItem(new Item(), "crystallized_squirrel_heart", true);
        WITCH_HAT = registerItem(
                new WitchCostumeItem(ThaumcraftMaterials.ARMORMAT_SPECIAL, 0, EntityEquipmentSlot.HEAD, 2),
                "witch_hat", true
        );
        WITCH_ROBE = registerItem(
                new WitchCostumeItem(ThaumcraftMaterials.ARMORMAT_SPECIAL, 0, EntityEquipmentSlot.CHEST, 2),
                "witch_robe", true
        );
        WITCH_SKIRT = registerItem(
                new WitchCostumeItem(ThaumcraftMaterials.ARMORMAT_SPECIAL, 0, EntityEquipmentSlot.LEGS, 2),
                "witch_skirt", true
        );
        WITCH_BOOTS = registerItem(
                new WitchCostumeItem(ThaumcraftMaterials.ARMORMAT_SPECIAL, 0, EntityEquipmentSlot.FEET, 2),
                "witch_boots", true
        );
        DARK_WITCH_HAT = registerItem(
                new DarkWitchCostumeItem(ThaumcraftMaterials.ARMORMAT_VOIDROBE, 0, EntityEquipmentSlot.HEAD, 8),
                "dark_witch_hat", true
        );
        DARK_WITCH_ROBE = registerItem(
                new DarkWitchCostumeItem(ThaumcraftMaterials.ARMORMAT_VOIDROBE, 0, EntityEquipmentSlot.CHEST, 8),
                "dark_witch_robe", true
        );
        DARK_WITCH_SKIRT = registerItem(
                new DarkWitchCostumeItem(ThaumcraftMaterials.ARMORMAT_VOIDROBE, 0, EntityEquipmentSlot.LEGS, 8),
                "dark_witch_skirt", true
        );
        DARK_WITCH_BOOTS = registerItem(
                new DarkWitchCostumeItem(ThaumcraftMaterials.ARMORMAT_VOIDROBE, 0, EntityEquipmentSlot.FEET, 8),
                "dark_witch_boots", true
        );
        WITCH_BROOM = registerItem(new WitchBroomItem(), "witch_broom", true);
        WITCH_STAFF = registerItem(new WitchStaffItem(), "witch_staff", true);
        RISUNIC_FOCI = registerItem(new Item(), "risunic_foci", true);
        RISUNIC_FOCAL_POWER_BOOSTER = registerItem(
                new WitchStaffAttachmentItem(WitchStaffAttachmentItem.Type.POWER_BOOSTER),
                "risunic_focal_power_booster", true
        );
        RISUNIC_FOCAL_LINGERER = registerItem(
                new WitchStaffAttachmentItem(WitchStaffAttachmentItem.Type.LINGERER),
                "risunic_focal_lingerer", true
        );
        RISUNIC_FOCAL_PROJECTILE_BOOSTER = registerItem(
                new WitchStaffAttachmentItem(WitchStaffAttachmentItem.Type.PROJECTILE_BOOSTER),
                "risunic_focal_projectile_booster", true
        );
        WITCH_SHOTGUN = registerItem(new WitchShotgunItem(), "witch_shotgun", true);
    }

    public static <T extends Item> T registerItem(T item, String registryName, boolean canBeInCreative) {
        if (canBeInCreative) item.setCreativeTab(SquirrelWitchery.creativeItemsTab);
        if (item.getRegistryName() == null) item.setRegistryName(registryName);
        item.setTranslationKey(registryName);
        ITEMS.add(item);
        return item;
    }

    @SubscribeEvent
    public void onItemRegistry(RegistryEvent.Register<Item> e) {
        IForgeRegistry<Item> reg = e.getRegistry();
        reg.registerAll(ITEMS.toArray(new Item[0]));
    }
}
