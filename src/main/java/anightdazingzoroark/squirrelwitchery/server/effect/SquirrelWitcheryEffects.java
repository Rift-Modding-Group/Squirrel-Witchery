package anightdazingzoroark.squirrelwitchery.server.effect;

import anightdazingzoroark.squirrelwitchery.server.entity.SwarmerSquirrelEntity;
import anightdazingzoroark.squirrelwitchery.server.player.SquirrelPlayerProperties;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

public class SquirrelWitcheryEffects {
    public static Potion GWS;
    public static Potion SQUIRREL_SWARM;

    public static void registerEffects() {
        GWS = new GWSEffect().setRegistryName("gws").setPotionName("effect.gws.name");
        SQUIRREL_SWARM = new SquirrelSwarmEffect().setRegistryName("squirrel_swarm").setPotionName("effect.squirrel_swarm.name");
    }

    @SubscribeEvent
    public void onPotionRegistry(RegistryEvent.Register<Potion> event) {
        IForgeRegistry<Potion> registry = event.getRegistry();
        registry.register(GWS);
        registry.register(SQUIRREL_SWARM);
    }

    //---helpers for potion effects---
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPotionApplicable(@NotNull PotionEvent.PotionApplicableEvent event) {
        if (event.getEntityLiving() instanceof SwarmerSquirrelEntity && event.getPotionEffect().getPotion() == SQUIRREL_SWARM) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPotionRemoved(PotionEvent.PotionRemoveEvent event) {
        if (event.getPotion() == GWS) this.restoreSuppressedWarp(event.getEntityLiving());
    }

    @SubscribeEvent
    public void onPotionExpired(PotionEvent.PotionExpiryEvent event) {
        if (event.getPotionEffect() != null && event.getPotionEffect().getPotion() == GWS) {
            this.restoreSuppressedWarp(event.getEntityLiving());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerClone(PlayerEvent.Clone event) {
        this.restoreSuppressedWarp(event.getOriginal());
    }

    private void restoreSuppressedWarp(EntityLivingBase entityLivingBase) {
        if (!(entityLivingBase instanceof EntityPlayerMP player)) return;

        SquirrelPlayerProperties properties = SquirrelPlayerProperties.get(player);
        IPlayerWarp warp = ThaumcraftCapabilities.getWarp(player);
        if (properties == null || warp == null) return;

        int suppressedWarp = properties.getGWSSuppressedWarp();
        if (suppressedWarp <= 0) return;

        warp.add(IPlayerWarp.EnumWarpType.PERMANENT, suppressedWarp);
        properties.setGWSSuppressedWarp(0);
        warp.sync(player);
    }
}
