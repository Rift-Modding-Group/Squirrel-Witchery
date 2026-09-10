package anightdazingzoroark.squirrelwitchery.server.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.casters.ItemFocus;

import java.util.List;

public class WitchStaffItem extends ItemCaster implements IRisuniumConsumer {
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

    //start out full when obtained from creative tab
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) return;

        ItemStack stack = new ItemStack(this);
        this.setRisuniumAmount(stack, MAX_RISUNIUM);
        items.add(stack);
    }

    //staff uses risunium instead of vis
    @Override
    public boolean consumeVis(ItemStack stack, EntityPlayer player, float amount, boolean crafting, boolean simulate) {
        int risuniumCost = (int) Math.ceil(amount * this.getConsumptionModifier(stack, player, crafting));
        int risuniumAmount = this.getRisuniumAmount(stack);
        if (risuniumAmount < risuniumCost) return false;

        if (!simulate) {
            this.setRisuniumAmount(stack, risuniumAmount - risuniumCost);
            player.inventoryContainer.detectAndSendChanges();
        }
        return true;
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
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        ItemStack focusStack = this.getFocusStack(stack);
        ItemFocus focus = this.getFocus(stack);
        if (focus != null && focusStack != null && !focusStack.isEmpty()) {
            int risuniumCost = (int) Math.ceil(focus.getVisCost(focusStack));
            tooltip.add(TextFormatting.ITALIC + "" + TextFormatting.LIGHT_PURPLE + I18n.format(
                    "risunium_consumer.cost", risuniumCost
            ));
            tooltip.add(
                    TextFormatting.BOLD + "" + TextFormatting.ITALIC + "" + TextFormatting.GREEN + focus.getItemStackDisplayName(focusStack)
            );
            focus.addFocusInformation(focusStack, world, tooltip, flag);
        }
        tooltip.add(this.stringForDisplayAmount(stack));
    }
}
