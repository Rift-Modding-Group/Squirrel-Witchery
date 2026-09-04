package anightdazingzoroark.squirrelwitchery.server.items;

import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.essentia.BlockJarItem;
import thaumcraft.common.tiles.essentia.TileJarFillable;

/**
 * implement on any item that requires risunium to function
 * */
public interface IRisuniumConsumer {
    //helper for getting risunium from jar block in world by interacting w it
    default boolean getRisuniumFromJarBlock(EntityPlayer player, World world, BlockPos pos, EnumHand hand) {
        ItemStack broom = player.getHeldItem(hand);

        if (world.isRemote) return false;

        if (!(world.getTileEntity(pos) instanceof TileJarFillable jar)) return false;

        int current = this.getRisuniumAmount(broom);
        int needed = this.getMaxRisunium() - current;

        if (needed <= 0) return false;

        int available = jar.containerContains(SquirrelWitcheryAspects.RISUNIUM);
        if (available <= 0) return false;

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
            return true;
        }

        return false;
    }

    //helper for getting risunium from jar block in player inventory
    default boolean getRisuniumFromJarInInventory(EntityPlayer player, World world, EnumHand hand) {
        ItemStack broom = player.getHeldItem(hand);

        if (world.isRemote) return false;

        int current = this.getRisuniumAmount(broom);
        if (current > this.getMaxRisunium()) return false;

        int needed = this.getMaxRisunium() - current;
        if (needed <= 0) return false;

        //search in player inventory for warded jars w risunium inside
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!(stack.getItem() instanceof BlockJarItem jarItem)) continue;

            AspectList aspects = jarItem.getAspects(stack);
            if (aspects == null) continue;

            int available = aspects.getAmount(SquirrelWitcheryAspects.RISUNIUM);
            if (available <= 0) continue;

            //transfer total risunium from jars to the broom
            int transfer = Math.min(needed, available);
            aspects.remove(SquirrelWitcheryAspects.RISUNIUM, transfer);
            jarItem.setAspects(stack, aspects);
            this.setRisuniumAmount(broom, current + transfer);
            world.playSound(
                    null,
                    player.posX, player.posY, player.posZ,
                    SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS,
                    0.7f, 1f
            );
            player.inventoryContainer.detectAndSendChanges();

            return true;
        }
        return false;
    }

    //helper for showin risunium amount in tooltips
    @NotNull
    default String stringForDisplayAmount(@NotNull ItemStack stack) {
        return TextFormatting.LIGHT_PURPLE + I18n.format("risunium_consumer.amount", this.getRisuniumAmount(stack), this.getMaxRisunium());
    }

    int getRisuniumAmount(@NotNull ItemStack stack);

    void setRisuniumAmount(@NotNull ItemStack stack, int amount);

    int getMaxRisunium();
}
