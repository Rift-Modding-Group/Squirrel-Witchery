package anightdazingzoroark.squirrelwitchery.server.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.casters.ItemCaster;

import java.util.List;
import java.util.Locale;

public class WitchStaffItem extends ItemCaster implements IRisuniumConsumer {
    public static final int ATTACHMENT_RISUNIUM_COST = 5;

    public WitchStaffItem() {
        super("witch_staff", 0);
        //required to not fuck up transforms for some reason
        ConfigItems.ITEM_VARIANT_HOLDERS.remove(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return false;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) return;

        ItemStack stack = new ItemStack(this);
        this.setRisuniumAmount(stack, MAX_RISUNIUM);
        items.add(stack);
    }

    @Override
    public EnumActionResult onItemUseFirst(
            EntityPlayer player, World world, BlockPos pos, EnumFacing side,
            float hitX, float hitY, float hitZ, EnumHand hand
    ) {
        if (this.getRisuniumFromJarBlock(player, world, pos, hand)) return EnumActionResult.SUCCESS;
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking() && this.getRisuniumFromJarInInventory(player, world, hand)) {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        if (!world.isRemote) {
            if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setBoolean("Casting", true);
        }

        ActionResult<ItemStack> result = super.onItemRightClick(world, player, hand);
        if (!world.isRemote && stack.hasTagCompound()) stack.getTagCompound().removeTag("Casting");
        return result;
    }

    @Nullable
    public WitchStaffAttachmentItem.Type getAttachment(@NotNull ItemStack stack) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Attachment", 1)) return null;

        WitchStaffAttachmentItem.Type[] attachmentTypes = WitchStaffAttachmentItem.Type.values();
        byte ordinal = stack.getTagCompound().getByte("Attachment");
        return ordinal >= 0 && ordinal < attachmentTypes.length ? attachmentTypes[ordinal] : null;
    }

    public void setAttachment(@NotNull ItemStack stack, @Nullable WitchStaffAttachmentItem.Type attachment) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setByte("Attachment", (byte) (attachment == null ? -1 : attachment.ordinal()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        tooltip.add(this.stringForDisplayAmount(stack));

        WitchStaffAttachmentItem.Type attachment = this.getAttachment(stack);
        if (attachment != null) {
            tooltip.add(TextFormatting.GOLD + I18n.format("witch_staff.attachments"));
            tooltip.add(TextFormatting.GRAY + " - " + I18n.format(
                    "witch_staff.attachment." + attachment.name().toLowerCase(Locale.ROOT)
            ));
        }
    }
}
