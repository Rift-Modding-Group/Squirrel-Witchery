package anightdazingzoroark.squirrelwitchery.server.items;

import anightdazingzoroark.squirrelwitchery.server.entity.WitchBroomEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WitchBroomItem extends Item implements IRisuniumConsumer {
    public static final int MAX_RISUNIUM = 250;

    public WitchBroomItem() {
        super();
        this.setMaxStackSize(1);
    }

    //start out full when obtained from creative tab
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) return;

        ItemStack stack = new ItemStack(this);
        this.setRisuniumAmount(stack, MAX_RISUNIUM);
        items.add(stack);
    }

    //deal with right clickin on risunium jar to refill risunium
    @Override
    public EnumActionResult onItemUseFirst(
            EntityPlayer player, World world, BlockPos pos, EnumFacing side,
            float hitX, float hitY, float hitZ, EnumHand hand
    ) {
        boolean result = this.getRisuniumFromJarBlock(player, world, pos, hand);
        return result ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
    }

    //deal w spawning broom as well as refilling from jar in inventory
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (worldIn.isRemote) return new ActionResult<>(EnumActionResult.PASS, itemstack);

        //block riding
        if (playerIn.isRiding()) return new ActionResult<>(EnumActionResult.FAIL, itemstack);

        //if sneaking, refill when player has risunium jars
        if (playerIn.isSneaking()) {
            boolean result = this.getRisuniumFromJarInInventory(playerIn, worldIn, handIn);
            return result ? new ActionResult<>(EnumActionResult.SUCCESS, itemstack) : new ActionResult<>(EnumActionResult.FAIL, itemstack);
        }
        //otherwise, summon witch broom
        else {
            int risuniumAmount = this.getRisuniumAmount(itemstack);
            if (risuniumAmount <= 0) return new ActionResult<>(EnumActionResult.FAIL, itemstack);

            //summon witch broom
            WitchBroomEntity witchBroomEntity = new WitchBroomEntity(worldIn);
            witchBroomEntity.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
            witchBroomEntity.setRisuniumAmount(risuniumAmount);
            worldIn.spawnEntity(witchBroomEntity);
            playerIn.startRiding(witchBroomEntity, true);

            //remove item
            itemstack.shrink(1);

            return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(this.stringForDisplayAmount(stack));
    }

    //---risunium storage management---
    @Override
    public int getRisuniumAmount(@NotNull ItemStack stack) {
        if (!stack.hasTagCompound() || stack.getTagCompound() == null) return 0;
        return stack.getTagCompound().getInteger("Risunium");
    }

    @Override
    public void setRisuniumAmount(@NotNull ItemStack stack, int amount) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }

        tag.setInteger("Risunium", Math.clamp(amount, 0, MAX_RISUNIUM));
    }

    @Override
    public int getMaxRisunium() {
        return MAX_RISUNIUM;
    }
}
