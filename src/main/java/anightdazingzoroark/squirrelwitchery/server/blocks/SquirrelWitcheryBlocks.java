package anightdazingzoroark.squirrelwitchery.server.blocks;

import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class SquirrelWitcheryBlocks {
    private static final List<Block> BLOCKS = new ArrayList<>();
    public static Block SQUIRREL_PLUSH;

    public static void registerBlocks() {
        SQUIRREL_PLUSH = registerBlock(new SquirrelPlushBlock(), "squirrel_plush", true, true);
    }

    private static <T extends Block> T registerBlock(T block, String registryName, boolean hasItem, boolean canBeInCreative) {
        if (canBeInCreative) block.setCreativeTab(SquirrelWitchery.creativeItemsTab);
        block.setRegistryName(registryName);
        block.setTranslationKey(registryName);
        BLOCKS.add(block);
        if (hasItem) SquirrelWitcheryItems.registerBlockItem(block, registryName, canBeInCreative);
        return block;
    }

    @SubscribeEvent
    public void onBlockRegistry(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(BLOCKS.toArray(new Block[0]));
    }
}
