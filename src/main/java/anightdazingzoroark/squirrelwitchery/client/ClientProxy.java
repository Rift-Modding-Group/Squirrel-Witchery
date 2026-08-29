package anightdazingzoroark.squirrelwitchery.client;

import anightdazingzoroark.riftlib.renderers.geo.GeoArmorRenderer;
import anightdazingzoroark.squirrelwitchery.client.renderer.SquirrelRenderer;
import anightdazingzoroark.squirrelwitchery.client.renderer.WitchCostumeRenderer;
import anightdazingzoroark.squirrelwitchery.server.ServerProxy;
import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.blocks.SquirrelWitcheryBlocks;
import anightdazingzoroark.squirrelwitchery.server.entity.SquirrelEntity;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends ServerProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        //---entity rendering---
        RenderingRegistry.registerEntityRenderingHandler(SquirrelEntity.class, SquirrelRenderer::new);

        //---crystal risunium---
        ModelResourceLocation crystalModel = new ModelResourceLocation("thaumcraft:crystal_aer", "normal");
        ModelLoader.setCustomStateMapper(SquirrelWitcheryBlocks.CRYSTAL_RISUNIUM, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(net.minecraft.block.state.IBlockState state) {
                return crystalModel;
            }
        });
        ModelLoader.setCustomModelResourceLocation(
                SquirrelWitcheryBlocks.CRYSTAL_RISUNIUM_ITEM,
                0,
                new ModelResourceLocation("thaumcraft:crystal_aer", "inventory")
        );
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
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                SquirrelWitcheryBlocks.CRYSTAL_RISUNIUM_ITEM,
                0,
                new ModelResourceLocation("thaumcraft:crystal_aer", "inventory")
        );

        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(
                (state, world, pos, tintIndex) -> SquirrelWitcheryAspects.RISUNIUM.getColor(),
                SquirrelWitcheryBlocks.CRYSTAL_RISUNIUM
        );
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
                (stack, tintIndex) -> SquirrelWitcheryAspects.RISUNIUM.getColor(),
                SquirrelWitcheryBlocks.CRYSTAL_RISUNIUM_ITEM
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
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
}
