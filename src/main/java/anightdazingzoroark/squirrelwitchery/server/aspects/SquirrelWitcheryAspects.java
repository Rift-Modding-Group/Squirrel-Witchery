package anightdazingzoroark.squirrelwitchery.server.aspects;

import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.ImmutablePair;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.AspectRegistryEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SquirrelWitcheryAspects {
    private static final Map<Item, ImmutablePair<Aspect, Integer>[]> ASPECT_ASSIGNMENTS = new HashMap<>();
    public static final Aspect RISUNIUM = new Aspect(
            "risunium",
            0xFB8E9A,
            null,
            new ResourceLocation(SquirrelWitchery.MODID, "textures/aspects/risunium.png"),
            1
    );

    public static void assignAspectsToItems() {
        assignAspectsToItem(SquirrelWitcheryItems.NUT, new ImmutablePair<>(RISUNIUM, 15));
        assignAspectsToItem(
                SquirrelWitcheryItems.WITCH_HAT,
                new ImmutablePair<>(RISUNIUM, 89), new ImmutablePair<>(Aspect.MAGIC, 15), new ImmutablePair<>(Aspect.SENSES, 29),
                new ImmutablePair<>(Aspect.LIGHT, 64), new ImmutablePair<>(Aspect.DARKNESS, 64), new ImmutablePair<>(Aspect.CRAFT, 18)
        );
        assignAspectsToItem(
                SquirrelWitcheryItems.WITCH_ROBE,
                new ImmutablePair<>(RISUNIUM, 140), new ImmutablePair<>(Aspect.MAGIC, 14), new ImmutablePair<>(Aspect.EXCHANGE, 37),
                new ImmutablePair<>(Aspect.SOUL, 69), new ImmutablePair<>(Aspect.LIFE, 48), new ImmutablePair<>(Aspect.DEATH, 48),
                new ImmutablePair<>(Aspect.CRAFT, 31)
        );
        assignAspectsToItem(
                SquirrelWitcheryItems.WITCH_SKIRT,
                new ImmutablePair<>(RISUNIUM, 125), new ImmutablePair<>(Aspect.MAGIC, 13),
                new ImmutablePair<>(Aspect.ORDER, 32), new ImmutablePair<>(Aspect.ENTROPY, 32),
                new ImmutablePair<>(Aspect.CRAFT, 27)
        );
        assignAspectsToItem(
                SquirrelWitcheryItems.WITCH_BOOTS,
                new ImmutablePair<>(RISUNIUM, 78), new ImmutablePair<>(Aspect.MAGIC, 12), new ImmutablePair<>(Aspect.CRAFT, 9)
        );
    }

    private static void assignAspectsToItem(Item item, ImmutablePair<Aspect, Integer>... aspects) {
        ASPECT_ASSIGNMENTS.put(item, aspects);
    }

    @SubscribeEvent
    public void onAspectRegistry(AspectRegistryEvent event) {
        for (Map.Entry<Item, ImmutablePair<Aspect, Integer>[]> aspectEntry : ASPECT_ASSIGNMENTS.entrySet()) {
            ItemStack itemStack = new ItemStack(aspectEntry.getKey());
            AspectList aspects = AspectHelper.getObjectAspects(itemStack);
            aspects = aspects == null ? new AspectList() : aspects.copy();

            for (ImmutablePair<Aspect, Integer> aspectAssignment : aspectEntry.getValue()) {
                aspects.merge(aspectAssignment.getLeft(), aspectAssignment.getRight());
            }
            event.register.registerObjectTag(itemStack, aspects);
        }
    }
}
