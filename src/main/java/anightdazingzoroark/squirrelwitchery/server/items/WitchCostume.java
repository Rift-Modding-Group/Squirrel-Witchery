package anightdazingzoroark.squirrelwitchery.server.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.items.IVisDiscountGear;

public class WitchCostume extends ItemArmor implements IVisDiscountGear {
    private final int visDiscount;

    public WitchCostume(int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, int visDiscount) {
        super(ThaumcraftMaterials.ARMORMAT_VOIDROBE, renderIndexIn, equipmentSlotIn);
        this.visDiscount = visDiscount;
    }

    @Override
    public int getVisDiscount(ItemStack itemStack, EntityPlayer entityPlayer) {
        return this.visDiscount;
    }
}
