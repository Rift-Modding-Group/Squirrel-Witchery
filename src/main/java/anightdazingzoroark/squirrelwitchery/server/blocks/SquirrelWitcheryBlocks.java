package anightdazingzoroark.squirrelwitchery.server.blocks;

import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.blocks.world.ore.BlockCrystal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SquirrelWitcheryBlocks {
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final Map<Block, ItemBlock> ITEM_BLOCKS = new HashMap<>();

    public static BlockCrystal CRYSTAL_RISUNIUM;
    public static ItemBlock CRYSTAL_RISUNIUM_ITEM;

    public static void registerBlocks() {
        CRYSTAL_RISUNIUM = registerBlock(
                new BlockCrystal("crystal_risunium", SquirrelWitcheryAspects.RISUNIUM),
                "crystal_risunium", true, true
        );
        CRYSTAL_RISUNIUM_ITEM = ITEM_BLOCKS.get(CRYSTAL_RISUNIUM);
    }

    private static <T extends Block> T registerBlock(T block, String registryName, boolean hasItem, boolean canBeInCreative) {
        if (!(block instanceof BlockCrystal)) block.setRegistryName(registryName);
        block.setTranslationKey(registryName);
        BLOCKS.add(block);

        if (hasItem) {
            ItemBlock itemBlock = SquirrelWitcheryItems.registerItem(new ItemBlock(block), registryName, canBeInCreative);
            ITEM_BLOCKS.put(block, itemBlock);
        }

        return block;
    }

    @SubscribeEvent
    public void onBlockRegistry(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(BLOCKS.toArray(new Block[0]));
    }
}
