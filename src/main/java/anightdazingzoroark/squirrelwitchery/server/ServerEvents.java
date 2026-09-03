package anightdazingzoroark.squirrelwitchery.server;

import anightdazingzoroark.squirrelwitchery.SquirrelWitcheryConfig;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchEvent;

import java.util.Random;

public class ServerEvents {
    @SubscribeEvent
    public void dropItemEvent(BlockEvent.HarvestDropsEvent event) {
        if (event.getWorld().isRemote || SquirrelWitcheryConfig.nutDropRate <= 0) return;

        //leaves must be broken
        if (event.getState().getMaterial() != Material.LEAVES) return;

        //drop rate for each of the nuts
        Random random = event.getWorld().rand;
        if (random.nextInt(SquirrelWitcheryConfig.nutDropRate) == 0) {
            int quantity = random.nextInt(SquirrelWitcheryConfig.nutDropQuantity[0], SquirrelWitcheryConfig.nutDropQuantity[1] + 1);
            event.getDrops().add(new ItemStack(SquirrelWitcheryItems.NUT, quantity));
        }
        if (random.nextInt(SquirrelWitcheryConfig.bigNutDropRate) == 0) {
            int quantity = random.nextInt(SquirrelWitcheryConfig.bigNutDropQuantity[0], SquirrelWitcheryConfig.bigNutDropQuantity[1] + 1);
            event.getDrops().add(new ItemStack(SquirrelWitcheryItems.BIG_NUT, quantity));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onItemPickup(EntityItemPickupEvent event) {
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP player)) return;

        ItemStack pickedUpStack = event.getItem().getItem();

        //unlock research tab after getting a nut
        if (pickedUpStack.getItem() == SquirrelWitcheryItems.NUT || pickedUpStack.getItem() == SquirrelWitcheryItems.BIG_NUT) {
            IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
            if (knowledge != null && knowledge.addResearch(SquirrelWitcheryResearch.FOUND_NUT)) {
                player.sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.format("message.found_nuts")), false);
                knowledge.sync(player);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onSquirrelScanned(ResearchEvent.Research event) {
        if (!"!squirrel".equals(event.getResearchKey()) || !(event.getPlayer() instanceof EntityPlayerMP player)) return;

        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (knowledge != null && knowledge.addResearch(SquirrelWitcheryResearch.FOUND_NUT)) {
            knowledge.sync(player);
        }
    }
}
