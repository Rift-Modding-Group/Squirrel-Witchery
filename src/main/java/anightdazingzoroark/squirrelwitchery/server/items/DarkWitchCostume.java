package anightdazingzoroark.squirrelwitchery.server.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.IWarpingGear;

public class DarkWitchCostume extends WitchCostume implements IWarpingGear {
    public DarkWitchCostume(ItemArmor.ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, int visDiscount) {
        super(materialIn, renderIndexIn, equipmentSlotIn, visDiscount);
    }

    @Override
    public int getWarp(ItemStack itemStack, EntityPlayer entityPlayer) {
        return 2;
    }
}
