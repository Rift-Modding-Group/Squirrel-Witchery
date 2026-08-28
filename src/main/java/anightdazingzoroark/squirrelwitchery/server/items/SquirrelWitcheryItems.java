package anightdazingzoroark.squirrelwitchery.server.items;

import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class SquirrelWitcheryItems {
    public static final List<Item> ITEMS = new ArrayList<>();

    public static Item NUT;
    public static ItemArmor WITCH_HAT;
    public static ItemArmor WITCH_ROBE;
    public static ItemArmor WITCH_SKIRT;
    public static ItemArmor WITCH_BOOTS;
    /*
    public static Item WITCH_STAFF;
    public static Item NUTSABER;
     */

    public static void registerItems() {
        NUT = registerItem(new Item(), "nut", true);
        WITCH_HAT = registerItem(
                new WitchCostume(0, EntityEquipmentSlot.HEAD, 2),
                "witch_hat", true
        );
        WITCH_ROBE = registerItem(
                new WitchCostume(0, EntityEquipmentSlot.CHEST, 2),
                "witch_robe", true
        );
        WITCH_SKIRT = registerItem(
                new WitchCostume(0, EntityEquipmentSlot.LEGS, 2),
                "witch_skirt", true
        );
        WITCH_BOOTS = registerItem(
                new WitchCostume(0, EntityEquipmentSlot.FEET, 2),
                "witch_boots", true
        );
    }

    public static <T extends Item> T registerItem(T item, String registryName, boolean canBeInCreative) {
        if (canBeInCreative) item.setCreativeTab(SquirrelWitchery.creativeItemsTab);
        item.setRegistryName(registryName);
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
