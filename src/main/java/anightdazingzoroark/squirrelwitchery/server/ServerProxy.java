package anightdazingzoroark.squirrelwitchery.server;

import anightdazingzoroark.riftlib.message.RiftLibMessage;
import anightdazingzoroark.riftlib.message.RiftLibMessageSide;
import anightdazingzoroark.riftlib.message.RiftLibMessageWrapper;
import anightdazingzoroark.riftlib.nbtStorageUser.propertySystem.registry.PropertyRegistry;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.blocks.SquirrelWitcheryBlocks;
import anightdazingzoroark.squirrelwitchery.server.entity.SquirrelWitcheryEntities;
import anightdazingzoroark.squirrelwitchery.server.effect.SquirrelWitcheryEffects;
import anightdazingzoroark.squirrelwitchery.server.focus.FocusEffectNutRain;
import anightdazingzoroark.squirrelwitchery.server.focus.FocusEffectSquirrelSwarm;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import anightdazingzoroark.squirrelwitchery.server.gui.SquirrelWitcheryGuiHandler;
import anightdazingzoroark.squirrelwitchery.server.message.MessageControlWitchBroom;
import anightdazingzoroark.squirrelwitchery.server.message.MessageOpenMagicalSquirrelInventory;
import anightdazingzoroark.squirrelwitchery.server.player.SquirrelPlayerProperties;
import anightdazingzoroark.squirrelwitchery.server.recipe.SquirrelWitcheryRecipes;
import anightdazingzoroark.squirrelwitchery.server.sounds.SquirrelWitcherySounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.casters.FocusEngine;

public class ServerProxy {
    public static RiftLibMessageWrapper<RiftLibMessage, RiftLibMessage> MESSAGE_WRAPPER = new RiftLibMessageWrapper<>(SquirrelWitchery.MODID);

    public void preInit(FMLPreInitializationEvent e) {
        //---registries---
        SquirrelWitcheryBlocks.registerBlocks();
        SquirrelWitcheryItems.registerItems();
        SquirrelWitcheryEffects.registerEffects();
        SquirrelWitcheryEntities.registerEntities();
        PropertyRegistry.register(
                SquirrelWitchery.MODID + ":player",
                new PropertyRegistry.ClassPropertyPair<>(EntityPlayer.class, SquirrelPlayerProperties::new)
        );

        //---messages---
        MESSAGE_WRAPPER.registerMessage(MessageControlWitchBroom.class, RiftLibMessageSide.SERVER);
        MESSAGE_WRAPPER.registerMessage(MessageOpenMagicalSquirrelInventory.class, RiftLibMessageSide.SERVER);

        NetworkRegistry.INSTANCE.registerGuiHandler(SquirrelWitchery.instance, new SquirrelWitcheryGuiHandler());

        //---events---
        MinecraftForge.EVENT_BUS.register(new SquirrelWitcheryBlocks());
        MinecraftForge.EVENT_BUS.register(new SquirrelWitcheryItems());
        MinecraftForge.EVENT_BUS.register(new SquirrelWitcheryEffects());
        MinecraftForge.EVENT_BUS.register(new SquirrelWitcherySounds());
        MinecraftForge.EVENT_BUS.register(new SquirrelWitcheryRecipes());
        MinecraftForge.EVENT_BUS.register(new ServerEvents());
    }

    public void init(FMLInitializationEvent e) {
        SquirrelWitcheryEntities.registerEntitySpawns();
        SquirrelWitcheryItems.addOreDicTags();
        SquirrelWitcheryAspects.assignAspects();
        SquirrelWitcheryResearch.registerCategory();
        FocusEngine.registerElement(
                FocusEffectNutRain.class,
                new ResourceLocation(SquirrelWitchery.MODID, "textures/focus/nut_rain_icon.png"),
                0xFB8E9A
        );
        FocusEngine.registerElement(
                FocusEffectSquirrelSwarm.class,
                new ResourceLocation(SquirrelWitchery.MODID, "textures/focus/squirrel_swarm_icon.png"),
                0xAE3E3E
        );
        SquirrelWitcheryRecipes.registerRecipes();

        //---events---
        MinecraftForge.EVENT_BUS.register(new SquirrelWitcheryAspects());
    }

    public void postInit(FMLPostInitializationEvent e) {}
}
