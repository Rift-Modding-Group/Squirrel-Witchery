package anightdazingzoroark.squirrelwitchery.server;

import anightdazingzoroark.squirrelwitchery.SpartanWeaponryUtils;
import anightdazingzoroark.squirrelwitchery.SquirrelWitcheryConfig;
import anightdazingzoroark.squirrelwitchery.TinkersConstructUtil;
import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import anightdazingzoroark.squirrelwitchery.server.entity.ai.MagicalSquirrelRangedAttackBehavior;
import anightdazingzoroark.squirrelwitchery.server.entity.WitchBroomEntity;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchEvent;

import java.util.Random;

public class ServerEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void recordSquirrelRangedHit(LivingHurtEvent event) {
        if (event.getEntityLiving().world.isRemote || event.getAmount() <= 0f
                || !(event.getSource().getTrueSource() instanceof MagicalSquirrelEntity squirrel)
        ) {
            return;
        }
        if (squirrel.getAttackBehavior() instanceof MagicalSquirrelRangedAttackBehavior behavior) behavior.recordRangedHit(event.getEntityLiving());
    }

    @SubscribeEvent
    public void applySquirrelTinkersLooting(LootingLevelEvent event) {
        if (event.getDamageSource() != null && event.getDamageSource().getTrueSource() instanceof MagicalSquirrelEntity squirrel
                && Loader.isModLoaded("tconstruct") && TinkersConstructUtil.isMeleeWeapon(squirrel.getHeldItemMainhand())) {
            event.setLootingLevel(Math.max(event.getLootingLevel(), TinkersConstructUtil.getLootingLevel(squirrel.getHeldItemMainhand())));
        }
    }

    @SubscribeEvent
    public void catchSquirrelThrownWeapon(ProjectileImpactEvent.Arrow event) {
        if (event.getRayTraceResult().entityHit instanceof MagicalSquirrelEntity squirrel
                && Loader.isModLoaded("spartanweaponry")
                && SpartanWeaponryUtils.catchThrownWeapon(squirrel, event.getArrow())
        ) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void enableArmorStandArmsForWitchWeapons(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getWorld().isRemote || !(event.getTarget() instanceof EntityArmorStand armorStand) || armorStand.getShowArms()) return;

        ItemStack stack = event.getItemStack();
        if (stack.getItem() == SquirrelWitcheryItems.WITCH_STAFF || stack.getItem() == SquirrelWitcheryItems.WITCH_SHOTGUN
                || stack.getItem() == SquirrelWitcheryItems.NUTSABER
        ) {
            armorStand.setShowArms(true);
        }
    }

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
            if (knowledge == null) return;

            boolean nutsCompleted = ThaumcraftApi.internalMethods.completeResearch(player, SquirrelWitcheryResearch.NUTS);
            boolean risuniumStudiesUnlocked = knowledge.addResearch(SquirrelWitcheryResearch.RISUNIUM_DISCOVERED);
            if (nutsCompleted) {
                player.sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.format("message.found_nuts")), false);
            }
            if (nutsCompleted || risuniumStudiesUnlocked) {
                knowledge.sync(player);
            }
        }
    }

    @SubscribeEvent
    public void onResearch(ResearchEvent.Research event) {
        if (!(event.getPlayer() instanceof EntityPlayerMP player)) return;
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (knowledge == null) return;

        //add risunium discovery when scanning the squirrels
        if ((event.getResearchKey().equals(SquirrelWitcheryResearch.SQUIRREL)
                || event.getResearchKey().equals(SquirrelWitcheryResearch.SQUIRREL_GUN)
                || event.getResearchKey().equals(SquirrelWitcheryResearch.MAGICAL_SQUIRREL)
        )
                && knowledge.addResearch(SquirrelWitcheryResearch.RISUNIUM_DISCOVERED)
        ) {
            knowledge.sync(player);
        }

        //add risunic devices when unlocking a risunic device research tab
        if (SquirrelWitcheryResearch.RISUNIC_DEVICE_TABS.contains(event.getResearchKey())
                && ThaumcraftApi.internalMethods.completeResearch(player, SquirrelWitcheryResearch.RISUNIC_DEVICES)
        ) {
            knowledge.sync(player);
        }
    }

    @SubscribeEvent
    public void onDismount(EntityMountEvent event) {
        //make sure that, when dismounting from a witch's broom, said witch's broom becomes an item
        if (event.isMounting() || event.getWorldObj().isRemote) return;

        if (event.getEntityBeingMounted() instanceof WitchBroomEntity witchBroomEntity && !witchBroomEntity.isDead) {
            witchBroomEntity.convertToItem();
        }
    }
}
