package anightdazingzoroark.squirrelwitchery.server.sounds;

import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SquirrelWitcherySounds {
    public static SoundEvent SQUIRREL_AMBIENT = new SoundEvent(
            new ResourceLocation(SquirrelWitchery.MODID, "squirrel_ambient")
    ).setRegistryName(SquirrelWitchery.MODID, "squirrel_ambient");
    public static SoundEvent SQUIRREL_HURT = new SoundEvent(
            new ResourceLocation(SquirrelWitchery.MODID, "squirrel_hurt")
    ).setRegistryName(SquirrelWitchery.MODID, "squirrel_hurt");
    public static SoundEvent SQUIRREL_DEATH = new SoundEvent(
            new ResourceLocation(SquirrelWitchery.MODID, "squirrel_death")
    ).setRegistryName(SquirrelWitchery.MODID, "squirrel_death");

    @SubscribeEvent
    public void onSoundRegistry(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(SQUIRREL_AMBIENT);
        event.getRegistry().register(SQUIRREL_HURT);
        event.getRegistry().register(SQUIRREL_DEATH);
    }
}
