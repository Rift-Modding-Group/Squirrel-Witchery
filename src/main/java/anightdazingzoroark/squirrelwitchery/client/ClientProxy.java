package anightdazingzoroark.squirrelwitchery.client;

import anightdazingzoroark.riftlib.renderers.geo.GeoArmorRenderer;
import anightdazingzoroark.riftlib.renderers.geo.GeoBlockRenderer;
import anightdazingzoroark.squirrelwitchery.client.renderer.armor.SquirrelFurDressRenderer;
import anightdazingzoroark.squirrelwitchery.client.renderer.block.SquirrelPlushBlockRenderer;
import anightdazingzoroark.squirrelwitchery.client.renderer.entity.*;
import anightdazingzoroark.squirrelwitchery.client.renderer.item.*;
import anightdazingzoroark.squirrelwitchery.client.renderer.armor.WitchCostumeRenderer;
import anightdazingzoroark.squirrelwitchery.client.renderer.player.SquirrelAttachmentsLayer;
import anightdazingzoroark.squirrelwitchery.server.ServerProxy;
import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.blocks.SquirrelPlushBlock;
import anightdazingzoroark.squirrelwitchery.server.blocks.SquirrelWitcheryBlocks;
import anightdazingzoroark.squirrelwitchery.server.entity.*;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends ServerProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        //---entity rendering---
        RenderingRegistry.registerEntityRenderingHandler(SquirrelEntity.class, SquirrelEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(WitchBroomEntity.class, WitchBroomEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ExplosiveNutProjectileEntity.class, ExplosiveNutEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SquirrelGunEntity.class, SquirrelGunEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MagicalSquirrelEntity.class, MagicalSquirrelEntityRenderer::new);

        //---item rendering---
        SquirrelWitcheryItems.WITCH_BROOM.setTileEntityItemStackRenderer(new WitchBroomItemRenderer());
        SquirrelWitcheryItems.WITCH_STAFF.setTileEntityItemStackRenderer(new WitchStaffItemRenderer());
        SquirrelWitcheryItems.WITCH_SHOTGUN.setTileEntityItemStackRenderer(new WitchShotgunItemRenderer());
        SquirrelWitcheryItems.NUTSABER.setTileEntityItemStackRenderer(new NutsaberItemRenderer());
        SquirrelWitcheryItems.SQUIRREL_TAIL_HAMMER.setTileEntityItemStackRenderer(new SquirrelTailHammerItemRenderer());
        SquirrelPlushItemRenderer squirrelPlushItemRenderer = new SquirrelPlushItemRenderer();
        SquirrelWitcheryItems.getBlockItem(SquirrelWitcheryBlocks.SQUIRREL_PLUSH).setTileEntityItemStackRenderer(squirrelPlushItemRenderer);
        SquirrelWitcheryItems.getBlockItem(SquirrelWitcheryBlocks.WITCH_SQUIRREL_PLUSH).setTileEntityItemStackRenderer(squirrelPlushItemRenderer);

        //---block rendering---
        SquirrelPlushBlockRenderer squirrelPlushBlockRenderer = new SquirrelPlushBlockRenderer();
        GeoBlockRenderer.registerBlockRenderer(SquirrelWitcheryBlocks.SQUIRREL_PLUSH, squirrelPlushBlockRenderer);
        GeoBlockRenderer.registerBlockRenderer(SquirrelWitcheryBlocks.WITCH_SQUIRREL_PLUSH, squirrelPlushBlockRenderer);

        //---events---
        MinecraftForge.EVENT_BUS.register(new SquirrelWitcheryOverlay());
        MinecraftForge.EVENT_BUS.register(new ClientEvents());

        //---others---
        SquirrelWitcheryControls.init();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);

        //register item models
        for (Item item : SquirrelWitcheryItems.ITEMS) {
            final String resName = item.getRegistryName().toString();
            final ModelResourceLocation res = new ModelResourceLocation(resName, "inventory");
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, res);
        }
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
                (stack, tintIndex) -> SquirrelWitcheryAspects.RISUNIUM.getColor(),
                SquirrelWitcheryItems.RISUNIC_FOCI
        );

        //register armor models
        WitchCostumeRenderer witchCostumeRenderer = new WitchCostumeRenderer();

        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.WITCH_HAT, witchCostumeRenderer);
        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.WITCH_ROBE, witchCostumeRenderer);
        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.WITCH_SKIRT, witchCostumeRenderer);
        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.WITCH_BOOTS, witchCostumeRenderer);

        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.DARK_WITCH_HAT, witchCostumeRenderer);
        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.DARK_WITCH_ROBE, witchCostumeRenderer);
        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.DARK_WITCH_SKIRT, witchCostumeRenderer);
        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.DARK_WITCH_BOOTS, witchCostumeRenderer);

        SquirrelFurDressRenderer squirrelFurDressRenderer = new SquirrelFurDressRenderer();
        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.SQUIRREL_FUR_HAT, squirrelFurDressRenderer);
        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.SQUIRREL_FUR_JACKET, squirrelFurDressRenderer);
        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.SQUIRREL_FUR_SKIRT, squirrelFurDressRenderer);
        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.SQUIRREL_FUR_SHOES, squirrelFurDressRenderer);

        //register player attachments
        for (RenderPlayer renderPlayer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
            renderPlayer.addLayer(new SquirrelAttachmentsLayer(renderPlayer));
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
}
