package anightdazingzoroark.squirrelwitchery.server;

import anightdazingzoroark.squirrelwitchery.SquirrelWitcheryConfig;
import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.blocks.SquirrelWitcheryBlocks;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.research.ResearchManager;

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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onItemPickup(EntityItemPickupEvent event) {
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP player)) return;

        ItemStack pickedUpStack = event.getItem().getItem();

        //unlock research tab after getting a nut
        if (pickedUpStack.getItem() == SquirrelWitcheryItems.NUT) {
            IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
            if (knowledge != null && knowledge.addResearch(SquirrelWitcheryResearch.FOUND_NUT)) {
                knowledge.sync(player);
            }
        }

        //unlock vis crystal risunium entry
        if (pickedUpStack.getItem() == ItemsTC.crystalEssence && ((IEssentiaContainerItem) pickedUpStack.getItem()).getAspects(pickedUpStack).getAmount(SquirrelWitcheryAspects.RISUNIUM) >= 1) {
            ResearchManager.completeResearch(player, SquirrelWitcheryResearch.VIS_CRYSTAL_RISUNIUM);
        }

        //unlock crystal risunium entry
        if (pickedUpStack.getItem() == Item.getItemFromBlock(SquirrelWitcheryBlocks.CRYSTAL_RISUNIUM)) {
            ResearchManager.completeResearch(player, SquirrelWitcheryResearch.CRYSTAL_RISUNIUM);
        }
    }
}
