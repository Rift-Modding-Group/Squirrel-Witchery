package anightdazingzoroark.squirrelwitchery.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class SquirrelWitcheryControls {
    public static KeyBinding broomUp = new KeyBinding("broom.go_up", Keyboard.KEY_SPACE, "key.categories.squirrel_witchery");
    public static KeyBinding broomDown = new KeyBinding("broom.go_down", Keyboard.KEY_X, "key.categories.squirrel_witchery");

    public static void init() {
        ClientRegistry.registerKeyBinding(broomUp);
        ClientRegistry.registerKeyBinding(broomDown);
    }
}
