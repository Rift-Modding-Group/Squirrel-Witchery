package anightdazingzoroark.squirrelwitchery.client.renderer.block;

import anightdazingzoroark.riftlib.block.AnimatedBlockStateHolder;
import anightdazingzoroark.riftlib.core.manager.AnimationDataBlock;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoBlockRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class SquirrelPlushBlockRenderer extends GeoBlockRenderer<AnimatedBlockStateHolder> {
    public SquirrelPlushBlockRenderer() {
        super(
                new AnimatedGeoModel<AnimatedBlockStateHolder>() {
                    @Override
                    @NotNull
                    public String getModId() {
                        return SquirrelWitchery.MODID;
                    }

                    @Override
                    @NotNull
                    public String getModelIdentifier(AnimatedBlockStateHolder animatedBlockStateHolder) {
                        return "geometry.squirrel_plush";
                    }

                    @Override
                    @NotNull
                    public String getTextureLocation(AnimatedBlockStateHolder animatedBlockStateHolder) {
                        return "blocks/squirrel_plush.png";
                    }
                },
                (world, blockPos, blockState) -> {
                    return new AnimatedBlockStateHolder(world, blockPos, blockState) {
                        @Override
                        public void initializeAnimationData(@NonNull AnimationDataBlock animData) {
                            animData.setScale(0.5f);
                        }
                    };
                }
        );
    }
}
