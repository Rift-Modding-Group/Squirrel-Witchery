package anightdazingzoroark.squirrelwitchery.server.aspects;

import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.blocks.SquirrelWitcheryBlocks;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(SquirrelWitcheryItems.NUT, false, new AspectPair(RISUNIUM, 5)));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(SquirrelWitcheryItems.BIG_NUT, false, new AspectPair(RISUNIUM, 35)));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(SquirrelWitcheryItems.SQUIRREL_FUR, false,
                new AspectPair(RISUNIUM, 10), new AspectPair(Aspect.BEAST, 10)
        ));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.WITCH_HAT, true,
                new AspectPair(RISUNIUM, 89), new AspectPair(Aspect.CRAFT, 7), new AspectPair(Aspect.MAGIC, 7)
        ));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.WITCH_ROBE, true,
                new AspectPair(RISUNIUM, 156), new AspectPair(Aspect.CRAFT, 36), new AspectPair(Aspect.MAGIC, 13)
        ));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.WITCH_SKIRT, true,
                new AspectPair(RISUNIUM, 136), new AspectPair(Aspect.CRAFT, 31), new AspectPair(Aspect.MAGIC, 12)
        ));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.WITCH_BOOTS, true,
                new AspectPair(RISUNIUM, 78), new AspectPair(Aspect.CRAFT, 18), new AspectPair(Aspect.MAGIC, 10)
        ));
        //note to self: in gimp, use 0x791aa1 to color dark witch costume
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.DARK_WITCH_HAT, true,
                new AspectPair(RISUNIUM, 89), new AspectPair(Aspect.MAGIC, 15),
                new AspectPair(Aspect.LIGHT, 64), new AspectPair(Aspect.DARKNESS, 64),
                new AspectPair(Aspect.CRAFT, 18), new AspectPair(Aspect.FLUX, 6)
        ));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.DARK_WITCH_ROBE, true,
                new AspectPair(RISUNIUM, 140), new AspectPair(Aspect.MAGIC, 14),
                new AspectPair(Aspect.LIFE, 48), new AspectPair(Aspect.DEATH, 48),
                new AspectPair(Aspect.CRAFT, 31), new AspectPair(Aspect.ELDRITCH, 10)
        ));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.DARK_WITCH_SKIRT, true,
                new AspectPair(RISUNIUM, 125), new AspectPair(Aspect.MAGIC, 13),
                new AspectPair(Aspect.ORDER, 32), new AspectPair(Aspect.ENTROPY, 32),
                new AspectPair(Aspect.CRAFT, 27), new AspectPair(Aspect.ELDRITCH, 8)
        ));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.DARK_WITCH_BOOTS, true,
                new AspectPair(RISUNIUM, 78), new AspectPair(Aspect.MAGIC, 12),
                new AspectPair(Aspect.CRAFT, 9), new AspectPair(Aspect.FLUX, 6)
        ));
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Item>(
                SquirrelWitcheryItems.WITCH_BROOM, true,
                new AspectPair(RISUNIUM, 51), new AspectPair(Aspect.FLIGHT, 46), new AspectPair(Aspect.MAGIC, 23),
                new AspectPair(Aspect.EARTH, 13)
        ));

        //---block assignments---
        ASPECT_ASSIGNMENTS.add(new AspectAssignment<Block>(
                SquirrelWitcheryBlocks.CRYSTAL_RISUNIUM, true, new AspectPair(RISUNIUM, 15)
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

            for (AspectPair aspectPair : itemAspectAssignment.aspects) {
                aspects.merge(aspectPair.aspect, aspectPair.amount);
            }
            event.register.registerObjectTag(itemStack, aspects);
        }
    }

    private record AspectAssignment<T> (T object, boolean clearExistingAspects, AspectPair... aspects) {}

    private record AspectPair(Aspect aspect, int amount) {}
}
