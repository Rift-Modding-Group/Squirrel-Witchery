package anightdazingzoroark.squirrelwitchery.server.aspects;

import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.blocks.SquirrelWitcheryBlocks;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.AspectRegistryEvent;

import java.util.ArrayList;
import java.util.List;

public final class SquirrelWitcheryAspects {
    private static final List<AspectAssignment<?>> ASPECT_ASSIGNMENTS = new ArrayList<>();
    public static final Aspect RISUNIUM = new Aspect(
            "risunium",
            0xFB8E9A,
            null,
            new ResourceLocation(SquirrelWitchery.MODID, "textures/aspects/risunium.png"),
            1
    );

    public static void assignAspects() {
        //---item assignments---
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(SquirrelWitcheryItems.NUT, false, new ImmutablePair<>(RISUNIUM, 5)));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.WITCH_HAT, true,
                new ImmutablePair<>(RISUNIUM, 89), new ImmutablePair<>(Aspect.CRAFT, 7), new ImmutablePair<>(Aspect.MAGIC, 7)
        ));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.WITCH_ROBE, true,
                new ImmutablePair<>(RISUNIUM, 156), new ImmutablePair<>(Aspect.CRAFT, 36), new ImmutablePair<>(Aspect.MAGIC, 13)
        ));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.WITCH_SKIRT, true,
                new ImmutablePair<>(RISUNIUM, 136), new ImmutablePair<>(Aspect.CRAFT, 31), new ImmutablePair<>(Aspect.MAGIC, 12)
        ));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.WITCH_BOOTS, true,
                new ImmutablePair<>(RISUNIUM, 78), new ImmutablePair<>(Aspect.CRAFT, 18), new ImmutablePair<>(Aspect.MAGIC, 10)
        ));
        //note to self: in gimp, use 0x791aa1 to color dark witch costume
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.DARK_WITCH_HAT, true,
                new ImmutablePair<>(RISUNIUM, 89), new ImmutablePair<>(Aspect.MAGIC, 15),
                new ImmutablePair<>(Aspect.LIGHT, 64), new ImmutablePair<>(Aspect.DARKNESS, 64),
                new ImmutablePair<>(Aspect.CRAFT, 18), new ImmutablePair<>(Aspect.FLUX, 6)
        ));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.DARK_WITCH_ROBE, true,
                new ImmutablePair<>(RISUNIUM, 140), new ImmutablePair<>(Aspect.MAGIC, 14),
                new ImmutablePair<>(Aspect.LIFE, 48), new ImmutablePair<>(Aspect.DEATH, 48),
                new ImmutablePair<>(Aspect.CRAFT, 31), new ImmutablePair<>(Aspect.ELDRITCH, 10)
        ));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.DARK_WITCH_SKIRT, true,
                new ImmutablePair<>(RISUNIUM, 125), new ImmutablePair<>(Aspect.MAGIC, 13),
                new ImmutablePair<>(Aspect.ORDER, 32), new ImmutablePair<>(Aspect.ENTROPY, 32),
                new ImmutablePair<>(Aspect.CRAFT, 27), new ImmutablePair<>(Aspect.ELDRITCH, 8)
        ));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.DARK_WITCH_BOOTS, true,
                new ImmutablePair<>(RISUNIUM, 78), new ImmutablePair<>(Aspect.MAGIC, 12),
                new ImmutablePair<>(Aspect.CRAFT, 9), new ImmutablePair<>(Aspect.FLUX, 6)
        ));

        //---block assignments---
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Block>(
                SquirrelWitcheryBlocks.CRYSTAL_RISUNIUM, true, new ImmutablePair<>(RISUNIUM, 15)
        ));

        //---entity assignments---
        ThaumcraftApi.registerEntityTag(
                "squirrel",
                new AspectList().add(RISUNIUM, 15).add(Aspect.BEAST, 10)
        );
    }

    @SubscribeEvent
    public void onAspectRegistry(AspectRegistryEvent event) {
        for (AspectAssignment<?> itemAspectAssignment : ASPECT_ASSIGNMENTS) {
            ItemStack itemStack;
            if (itemAspectAssignment.object instanceof Item item) itemStack = new ItemStack(item);
            else if (itemAspectAssignment.object instanceof Block block) itemStack = new ItemStack(block);
            else throw new RuntimeException();

            AspectList aspects;
            if (itemAspectAssignment.clearExistingAspects) aspects = new AspectList();
            else {
                aspects = AspectHelper.getObjectAspects(itemStack);
                aspects = aspects == null ? new AspectList() : aspects.copy();
            }

            for (ImmutablePair<Aspect, Integer> aspectAssignment : itemAspectAssignment.aspects) {
                aspects.merge(aspectAssignment.getLeft(), aspectAssignment.getRight());
            }
            event.register.registerObjectTag(itemStack, aspects);
        }
    }

    private record AspectAssignment<T> (T object, boolean clearExistingAspects, ImmutablePair<Aspect, Integer>... aspects) {
        @SafeVarargs
        public AspectAssignment {}
    }
}
