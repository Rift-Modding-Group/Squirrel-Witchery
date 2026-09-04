package anightdazingzoroark.squirrelwitchery.server.items;

import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.entity.WitchBroomEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
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
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.essentia.BlockJarItem;
import thaumcraft.common.tiles.essentia.TileJarFillable;

import java.util.List;

public class WitchBroomItem extends Item {
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
        ItemStack broom = player.getHeldItem(hand);

        if (world.isRemote) return EnumActionResult.PASS;

        if (!(world.getTileEntity(pos) instanceof TileJarFillable jar)) return EnumActionResult.PASS;

        int current = this.getRisuniumAmount(broom);
        int needed = MAX_RISUNIUM - current;

        if (needed <= 0) return EnumActionResult.PASS;

        int available = jar.containerContains(SquirrelWitcheryAspects.RISUNIUM);
        if (available <= 0) return EnumActionResult.PASS;

        int transfer = Math.min(needed, available);
        if (jar.takeFromContainer(SquirrelWitcheryAspects.RISUNIUM, transfer)) {
            this.setRisuniumAmount(broom, current + transfer);
            world.playSound(
                    null,
                    player.posX, player.posY, player.posZ,
                    SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS,
                    0.7f, 1f
            );
            player.inventoryContainer.detectAndSendChanges();
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    //deal w spawning broom as well as refilling from jar in inventory
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (worldIn.isRemote) return new ActionResult<>(EnumActionResult.PASS, itemstack);

        //block riding
        if (playerIn.isRiding()) return new ActionResult<>(EnumActionResult.FAIL, itemstack);

        int risuniumAmount = this.getRisuniumAmount(itemstack);

        //if sneaking, refill when player has risunium jars
        if (playerIn.isSneaking()) {
            if (risuniumAmount > MAX_RISUNIUM) return new ActionResult<>(EnumActionResult.FAIL, itemstack);

            int needed = 250 - risuniumAmount;
            if (needed <= 0) return new ActionResult<>(EnumActionResult.FAIL, itemstack);

            //search in player inventory for warded jars w risunium inside
            for (ItemStack stack : playerIn.inventory.mainInventory) {
                if (!(stack.getItem() instanceof BlockJarItem jarItem)) continue;

                AspectList aspects = jarItem.getAspects(stack);
                if (aspects == null) continue;

                int available = aspects.getAmount(SquirrelWitcheryAspects.RISUNIUM);
                if (available <= 0) continue;

                //transfer total risunium from jars to the broom
                int transfer = Math.min(needed, available);
                aspects.remove(SquirrelWitcheryAspects.RISUNIUM, transfer);
                jarItem.setAspects(stack, aspects);
                this.setRisuniumAmount(itemstack, risuniumAmount + transfer);
                worldIn.playSound(
                        null,
                        playerIn.posX, playerIn.posY, playerIn.posZ,
                        SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS,
                        0.7f, 1f
                );
                playerIn.inventoryContainer.detectAndSendChanges();

                return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
            }

            return new ActionResult<>(EnumActionResult.PASS, itemstack);
        }
        //otherwise, summon witch broom
        else {
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
        tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format("broom.risunium_amount", this.getRisuniumAmount(stack), MAX_RISUNIUM));
    }

    //---risunium storage management---
    public int getRisuniumAmount(@NotNull ItemStack stack) {
        if (!stack.hasTagCompound() || stack.getTagCompound() == null) return 0;
        return stack.getTagCompound().getInteger("Risunium");
    }

    public void setRisuniumAmount(@NotNull ItemStack stack, int amount) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }

        tag.setInteger("Risunium", Math.clamp(amount, 0, MAX_RISUNIUM));
    }
}
