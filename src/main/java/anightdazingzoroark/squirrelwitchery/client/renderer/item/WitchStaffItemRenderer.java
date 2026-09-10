package anightdazingzoroark.squirrelwitchery.client.renderer.item;

import anightdazingzoroark.riftlib.core.manager.AnimationDataItemStack;
import anightdazingzoroark.riftlib.item.AnimatedItemStackHolder;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoItemRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import org.jetbrains.annotations.NotNull;

public class WitchStaffItemRenderer extends GeoItemRenderer<AnimatedItemStackHolder> {
    public WitchStaffItemRenderer() {
        super(
                new AnimatedGeoModel<AnimatedItemStackHolder>() {
                    @Override
                    public @NotNull String getModId() {
                        return SquirrelWitchery.MODID;
                    }

                    @Override
                    public String getModelIdentifier(AnimatedItemStackHolder animatedItemStackHolder) {
                        return "geometry.witch_staff";
                    }

                    @Override
                    public String getTextureLocation(AnimatedItemStackHolder animatedItemStackHolder) {
                        return "items/witch_staff.png";
                    }
                },
                itemStack -> new AnimatedItemStackHolder(itemStack) {
                    @Override
                    public void initializeAnimationData(AnimationDataItemStack animationDataItemStack) {}
                }
        );
    }
}
