package anightdazingzoroark.squirrelwitchery.server;

import anightdazingzoroark.squirrelwitchery.SquirrelWitcheryConfig;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class ServerEvents {
    @SubscribeEvent
    public void dropItemEvent(BlockEvent.HarvestDropsEvent event) {
        if (event.getWorld().isRemote || SquirrelWitcheryConfig.nutDropRate <= 0) return;

        //leaves must be broken
        if (event.getState().getMaterial() != Material.LEAVES) return;

        //50% chance by default
        Random random = event.getWorld().rand;
        if (random.nextInt(SquirrelWitcheryConfig.nutDropRate) == 0) return;

        //add nuts to the block's drops
        int quantity = random.nextInt(SquirrelWitcheryConfig.nutDropQuantity[0], SquirrelWitcheryConfig.nutDropQuantity[1] + 1);
        event.getDrops().add(new ItemStack(SquirrelWitcheryItems.NUT, quantity));
    }
}
