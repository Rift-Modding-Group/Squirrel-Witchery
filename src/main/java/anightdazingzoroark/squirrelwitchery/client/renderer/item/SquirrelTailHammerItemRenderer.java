package anightdazingzoroark.squirrelwitchery.client.renderer.item;

import anightdazingzoroark.riftlib.core.manager.AnimationDataItemStack;
import anightdazingzoroark.riftlib.item.AnimatedItemStackHolder;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoItemRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class SquirrelTailHammerItemRenderer extends GeoItemRenderer<AnimatedItemStackHolder> {
    public SquirrelTailHammerItemRenderer() {
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
                        return "geometry.squirrel_tail_hammer";
                    }

                    @Override
                    @NotNull
                    public String getTextureLocation(AnimatedItemStackHolder animatedItemStackHolder) {
                        return "items/squirrel_tail_hammer.png";
                    }
                },
                itemStack -> new AnimatedItemStackHolder(itemStack) {
                    @Override
                    public void initializeAnimationData(@NonNull AnimationDataItemStack animData) {}
                }
        );
    }
}
