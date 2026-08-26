package anightdazingzoroark.squirrelwitchery.server;

import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy {
    public void preInit(FMLPreInitializationEvent e) {
        SquirrelWitcheryItems.registerItems();
        MinecraftForge.EVENT_BUS.register(new SquirrelWitcheryItems());
        MinecraftForge.EVENT_BUS.register(new ServerEvents());
    }

    public void init(FMLInitializationEvent e) {

    }

    public void postInit(FMLPostInitializationEvent e) {

    }
}
