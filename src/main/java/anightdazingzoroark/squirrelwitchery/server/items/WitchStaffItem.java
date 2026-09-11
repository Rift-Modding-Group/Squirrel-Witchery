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

    public boolean hasAttachment(@NotNull ItemStack stack, WitchStaffAttachmentItem.Type type) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Attachments", 10)) return false;
        return stack.getTagCompound().getCompoundTag("Attachments").getBoolean(type.name());
    }

    public boolean hasAnyAttachment(@NotNull ItemStack stack) {
        for (WitchStaffAttachmentItem.Type type : WitchStaffAttachmentItem.Type.values()) {
            if (this.hasAttachment(stack, type)) return true;
        }
        return false;
    }

    public void setAttachment(@NotNull ItemStack stack, WitchStaffAttachmentItem.Type type, boolean attached) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

        NBTTagCompound attachments;
        if (stack.getTagCompound().hasKey("Attachments", 10)) {
            attachments = stack.getTagCompound().getCompoundTag("Attachments");
        }
        else {
            attachments = new NBTTagCompound();
            stack.getTagCompound().setTag("Attachments", attachments);
        }

        if (attached) {
            for (WitchStaffAttachmentItem.Type attachmentType : WitchStaffAttachmentItem.Type.values()) {
                attachments.removeTag(attachmentType.name());
            }
            attachments.setBoolean(type.name(), true);
        }
        else attachments.removeTag(type.name());

        if (!this.hasAnyAttachment(stack)) stack.getTagCompound().removeTag("Attachments");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        tooltip.add(this.stringForDisplayAmount(stack));

        boolean hasAttachments = false;
        for (WitchStaffAttachmentItem.Type type : WitchStaffAttachmentItem.Type.values()) {
            if (!this.hasAttachment(stack, type)) continue;
            if (!hasAttachments) tooltip.add(TextFormatting.GOLD + I18n.format("witch_staff.attachments"));
            tooltip.add(TextFormatting.GRAY + " - " + I18n.format("witch_staff.attachment." + type.name().toLowerCase(Locale.ROOT)));
            hasAttachments = true;
        }
    }
}
