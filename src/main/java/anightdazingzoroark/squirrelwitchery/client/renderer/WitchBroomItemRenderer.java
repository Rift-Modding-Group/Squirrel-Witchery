package anightdazingzoroark.squirrelwitchery.client.renderer;

import anightdazingzoroark.riftlib.core.manager.AnimationDataItemStack;
import anightdazingzoroark.riftlib.item.AnimatedItemStackHolder;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoItemRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import org.jetbrains.annotations.NotNull;

public class WitchBroomItemRenderer extends GeoItemRenderer<AnimatedItemStackHolder> {
    public WitchBroomItemRenderer() {
        super(
                new AnimatedGeoModel<AnimatedItemStackHolder>() {
                    @Override
                    public @NotNull String getModId() {
                        return SquirrelWitchery.MODID;
                    }

                    @Override
                    public String getModelIdentifier(AnimatedItemStackHolder witchBroomItemHolder) {
                        return "geometry.witch_broom";
                    }

                    @Override
                    public String getTextureLocation(AnimatedItemStackHolder witchBroomItemHolder) {
                        return "entity/witch_broom.png";
                    }
                },
                itemStack -> new AnimatedItemStackHolder(itemStack) {
                    @Override
                    public void initializeAnimationData(AnimationDataItemStack animationDataItemStack) {}
                }
        );
    }
}
