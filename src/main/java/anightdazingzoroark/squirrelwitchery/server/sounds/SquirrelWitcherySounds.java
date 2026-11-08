package anightdazingzoroark.squirrelwitchery.server.sounds;

import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SquirrelWitcherySounds {
    @NotNull
    private static final List<SoundEvent> SOUND_EVENTS = new ArrayList<>();

    public static SoundEvent SQUIRREL_AMBIENT = defineSoundEvent("sounds.squirrelwitchery.squirrel_ambient");
    public static SoundEvent SQUIRREL_HURT = defineSoundEvent("sounds.squirrelwitchery.squirrel_hurt");
    public static SoundEvent SQUIRREL_DEATH = defineSoundEvent("sounds.squirrelwitchery.squirrel_death");

    public static SoundEvent MAGICAL_SQUIRREL_GREET = defineSoundEvent("sounds.squirrelwitchery.magical_squirrel_greet");
    public static SoundEvent MAGICAL_SQUIRREL_AMBIENT = defineSoundEvent("sounds.squirrelwitchery.magical_squirrel_ambient");
    public static SoundEvent MAGICAL_SQUIRREL_HURT = defineSoundEvent("sounds.squirrelwitchery.magical_squirrel_hurt");
    public static SoundEvent MAGICAL_SQUIRREL_DEATH = defineSoundEvent("sounds.squirrelwitchery.magical_squirrel_death");

    public static SoundEvent NUTSABER_ACTIVATE = defineSoundEvent("sounds.squirrelwitchery.nutsaber_activate");
    public static SoundEvent NUTSABER_SWING = defineSoundEvent("sounds.squirrelwitchery.nutsaber_swing");
    public static SoundEvent NUTSABER_DEACTIVATE = defineSoundEvent("sounds.squirrelwitchery.nutsaber_deactivate");

    private static SoundEvent defineSoundEvent(@NotNull String name) {
        SoundEvent toReturn = new SoundEvent(new ResourceLocation(SquirrelWitchery.MODID, name)).setRegistryName(SquirrelWitchery.MODID, name);
        SOUND_EVENTS.add(toReturn);
        return toReturn;
    }

    @SubscribeEvent
    public void onSoundRegistry(RegistryEvent.Register<SoundEvent> event) {
        for (SoundEvent soundEvent : SOUND_EVENTS) event.getRegistry().register(soundEvent);
    }
}
