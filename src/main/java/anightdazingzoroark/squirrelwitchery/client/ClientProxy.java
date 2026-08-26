package anightdazingzoroark.squirrelwitchery.client;

import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.renderers.geo.GeoArmorRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.client.renderer.WitchCostumeRenderer;
import anightdazingzoroark.squirrelwitchery.server.ServerProxy;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends ServerProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
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

        //register armor models
        WitchCostumeRenderer witchCostumeRenderer = new WitchCostumeRenderer();
        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.WITCH_HAT, witchCostumeRenderer);
        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.WITCH_ROBE, witchCostumeRenderer);
        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.WITCH_SKIRT, witchCostumeRenderer);
        GeoArmorRenderer.registerArmorRenderer(SquirrelWitcheryItems.WITCH_BOOTS, witchCostumeRenderer);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
}
