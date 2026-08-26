package anightdazingzoroark.squirrelwitchery.server;

import anightdazingzoroark.squirrelwitchery.SquirrelWitcheryConfig;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class ServerEvents {
    @SubscribeEvent
    public void dropItemEvent(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote || SquirrelWitcheryConfig.nutDropRate <= 0) return;

        //leaves must be broken
        BlockPos breakPos = event.getPos();
        if (event.getWorld().getBlockState(breakPos).getMaterial() != Material.LEAVES) return;

        //50% chance by default
        Random random = event.getWorld().rand;
        if (random.nextInt(SquirrelWitcheryConfig.nutDropRate) == 0) return;

        //apply block break and drop items
        EntityItem entityItem = new EntityItem(event.getWorld());
        entityItem.setItem(new ItemStack(SquirrelWitcheryItems.NUT, random.nextInt(SquirrelWitcheryConfig.nutDropQuantity[0], SquirrelWitcheryConfig.nutDropQuantity[1] + 1)));
        entityItem.setPosition(breakPos.getX(), breakPos.getY() + 0.5D, breakPos.getZ());
        event.getWorld().spawnEntity(entityItem);
    }
}
