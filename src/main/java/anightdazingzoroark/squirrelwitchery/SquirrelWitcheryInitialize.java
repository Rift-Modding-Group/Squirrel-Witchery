package anightdazingzoroark.squirrelwitchery;

import anightdazingzoroark.riftlib.RiftLib;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = SquirrelWitcheryInitialize.MODID,
        name = SquirrelWitcheryInitialize.MODNAME,
        version = SquirrelWitcheryInitialize.MODVERSION,
        dependencies = "required-after:riftlib@[1.0.0,);" +
                ";required-after:thaumcraft" +
                ";required-after:thaumicapi"
)
public class SquirrelWitcheryInitialize {
    public static final String MODID = "squirrelwitchery";
    public static final String MODNAME = "Squirrel Witchery";
    public static final String MODVERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);

    @SidedProxy(
            modId = SquirrelWitcheryInitialize.MODID,
            clientSide = "anightdazingzoroark.squirrelwitchery.client.ClientProxy",
            serverSide = "anightdazingzoroark.squirrelwitchery.server.ServerProxy"
    )
    public static IProxy PROXY;

    @Mod.Instance(MODID)
    public static SquirrelWitcheryInitialize instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PROXY.preInit(event);
        LOGGER.info("Hello From {}!", SquirrelWitcheryInitialize.MODNAME);
        LOGGER.info("Proxy is {}", PROXY);
        LOGGER.info("Language: {}", Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage());
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
