package anightdazingzoroark.squirrelwitchery.server;

import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.blocks.SquirrelWitcheryBlocks;
import anightdazingzoroark.squirrelwitchery.server.entity.SquirrelWitcheryEntities;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import anightdazingzoroark.squirrelwitchery.server.sounds.SquirrelWitcherySounds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy {
    public void preInit(FMLPreInitializationEvent e) {
        SquirrelWitcheryBlocks.registerBlocks();
        SquirrelWitcheryItems.registerItems();
        SquirrelWitcheryEntities.registerEntities();
        SquirrelWitcheryAspects.assignAspects();

        MinecraftForge.EVENT_BUS.register(new SquirrelWitcheryBlocks());
        MinecraftForge.EVENT_BUS.register(new SquirrelWitcheryItems());
        MinecraftForge.EVENT_BUS.register(new SquirrelWitcheryAspects());
        MinecraftForge.EVENT_BUS.register(new SquirrelWitcherySounds());
        MinecraftForge.EVENT_BUS.register(new ServerEvents());
    }

    public void init(FMLInitializationEvent e) {
        SquirrelWitcheryResearch.registerCategory();
        SquirrelWitcheryRecipes.registerRecipes();
    }

    public void postInit(FMLPostInitializationEvent e) {
        SquirrelWitcheryRecipes.configureResearch();
    }
}
