package anightdazingzoroark.squirrelwitchery.client.renderer.item;

import anightdazingzoroark.riftlib.core.manager.AnimationDataItemStack;
import anightdazingzoroark.riftlib.item.AnimatedItemStackHolder;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoItemRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
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
                        return "geometry.squirrel_plush";
                    }

                    @Override
                    @NotNull
                    public String getTextureLocation(AnimatedItemStackHolder animatedItemStackHolder) {
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
