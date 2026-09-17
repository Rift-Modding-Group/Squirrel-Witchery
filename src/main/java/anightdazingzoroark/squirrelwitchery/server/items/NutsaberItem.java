package anightdazingzoroark.squirrelwitchery.server.items;

import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NutsaberItem extends Item implements IRisuniumConsumer {
    private static final int RISUNIUM_PER_SWING = 5;

    public NutsaberItem() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) return;

        ItemStack stack = new ItemStack(this);
        this.setRisuniumAmount(stack, this.getMaxRisunium());
        items.add(stack);
    }

    @Override
    public EnumActionResult onItemUseFirst(
            EntityPlayer player, World world, BlockPos pos, EnumFacing side,
            float hitX, float hitY, float hitZ, EnumHand hand
    ) {
        if (this.getRisuniumFromJarBlock(player, world, pos, hand)) return EnumActionResult.SUCCESS;
        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.isSneaking()) return new ActionResult<>(EnumActionResult.PASS, stack);
        if (world.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, stack);

        boolean refilled = this.getRisuniumFromJarInInventory(player, world, hand);
        return new ActionResult<>(refilled ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote || !(entity instanceof EntityLivingBase entityLiving)) return;
        if (entityLiving.getHeldItemMainhand() != stack && entityLiving.getHeldItemOffhand() != stack) return;
        if (entityLiving.ticksExisted % 60 != 0 || this.getRisuniumAmount(stack) <= 0) return;

        //drain 1 risunium every 3 seconds when held
        this.setRisuniumAmount(stack, this.getRisuniumAmount(stack) - 1);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        if (!entityLiving.world.isRemote && this.getRisuniumAmount(stack) > 0) {
            this.setRisuniumAmount(stack, this.getRisuniumAmount(stack) - RISUNIUM_PER_SWING);
        }
        return false;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
        if (slot == EntityEquipmentSlot.MAINHAND && this.getRisuniumAmount(stack) >= RISUNIUM_PER_SWING) {
            modifiers.put(
                    SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Nutsaber modifier", 15f, 0)
            );
        }
        return modifiers;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(this.stringForDisplayAmount(stack));
    }

    @Override
    public int getMaxRisunium() {
        return 250;
    }
}
