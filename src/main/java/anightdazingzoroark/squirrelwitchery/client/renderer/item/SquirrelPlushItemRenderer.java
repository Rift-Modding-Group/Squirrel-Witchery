package anightdazingzoroark.squirrelwitchery.client.renderer.item;

import anightdazingzoroark.riftlib.core.manager.AnimationDataItemStack;
import anightdazingzoroark.riftlib.item.AnimatedItemStackHolder;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoItemRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.blocks.SquirrelWitcheryBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class SquirrelPlushItemRenderer extends GeoItemRenderer<AnimatedItemStackHolder> {
    public SquirrelPlushItemRenderer() {
        super(
                new AnimatedGeoModel<AnimatedItemStackHolder>() {
                    @Override
                    @NotNull
                    public String getModId() {
                        return SquirrelWitchery.MODID;
                    }

                    @Override
                    @NotNull
                    public String getModelIdentifier(AnimatedItemStackHolder animatedItemStackHolder) {
                        Item item = animatedItemStackHolder.getStack().getItem();
                        if (!(item instanceof ItemBlock itemBlock)) return "geometry.squirrel_plush";

                        if (itemBlock.getBlock() == SquirrelWitcheryBlocks.WITCH_SQUIRREL_PLUSH) {
                            return "geometry.witch_squirrel_plush";
                        }
                        return "geometry.squirrel_plush";
                    }

                    @Override
                    @NotNull
                    public String getTextureLocation(AnimatedItemStackHolder animatedItemStackHolder) {
                        Item item = animatedItemStackHolder.getStack().getItem();
                        if (!(item instanceof ItemBlock itemBlock)) return "blocks/squirrel_plush.png";

                        if (itemBlock.getBlock() == SquirrelWitcheryBlocks.WITCH_SQUIRREL_PLUSH) {
                            return "blocks/witch_squirrel_plush.png";
                        }
                        return "blocks/squirrel_plush.png";
                    }
                },
                itemStack -> new AnimatedItemStackHolder(itemStack) {
                    @Override
                    public void initializeAnimationData(@NonNull AnimationDataItemStack animData) {}
                }
        );
    }
}
