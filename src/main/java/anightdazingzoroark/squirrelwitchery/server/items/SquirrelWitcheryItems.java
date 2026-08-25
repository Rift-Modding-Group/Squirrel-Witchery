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
    /*
    public static ItemArmor WITCH_HAT;
    public static ItemArmor WITCH_ROBE;
    public static ItemArmor WITCH_SKIRT;
    public static ItemArmor WITCH_BOOTS;
     */

    public static void registerItems() {
        NUT = registerItem(new Item(), "nut", true);
        /*
        WITCH_HAT = (ItemArmor) registerItem(
                new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.HEAD),
                "witch_hat", true
        );
        WITCH_ROBE = (ItemArmor) registerItem(
                new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.CHEST),
                "witch_hat", true
        );
        WITCH_SKIRT = (ItemArmor) registerItem(
                new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.LEGS),
                "witch_hat", true
        );
        WITCH_BOOTS = (ItemArmor) registerItem(
                new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.FEET),
                "witch_hat", true
        );
         */
    }

    private static Item registerItem(Item item, String registryName, boolean canBeInCreative) {
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
