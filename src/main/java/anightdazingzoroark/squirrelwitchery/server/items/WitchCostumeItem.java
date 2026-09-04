package anightdazingzoroark.squirrelwitchery.server.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.IVisDiscountGear;

public class WitchCostumeItem extends ItemArmor implements IVisDiscountGear {
    private final int visDiscount;

    public WitchCostumeItem(ItemArmor.ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, int visDiscount) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
        this.visDiscount = visDiscount;
    }

    @Override
    public int getVisDiscount(ItemStack itemStack, EntityPlayer entityPlayer) {
        return this.visDiscount;
    }
}
