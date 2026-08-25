package anightdazingzoroark.squirrelwitchery;

import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.squirrelwitchery.server.ServerProxy;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = SquirrelWitchery.MODID,
        name = SquirrelWitchery.MODNAME,
        version = SquirrelWitchery.MODVERSION,
        dependencies = "required-after:riftlib@[1.0.0,);" +
                ";required-after:thaumcraft" +
                ";required-after:thaumicapi"
)
public class SquirrelWitchery {
    public static final String MODID = "squirrelwitchery";
    public static final String MODNAME = "Squirrel Witchery";
    public static final String MODVERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);
    public static final CreativeTabs creativeItemsTab = new CreativeTabs(MODID) {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(SquirrelWitcheryItems.NUT);
        }

        @Override
        public String getTranslationKey() {
            return "itemGroup.squirrel_witchery";
        }
    };

    @SidedProxy(
            modId = SquirrelWitchery.MODID,
            clientSide = "anightdazingzoroark.squirrelwitchery.client.ClientProxy",
            serverSide = "anightdazingzoroark.squirrelwitchery.server.ServerProxy"
    )
    public static ServerProxy PROXY;

    @Mod.Instance(MODID)
    public static SquirrelWitchery instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PROXY.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        RiftLib.initialize();
        PROXY.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit(event);
    }
}
