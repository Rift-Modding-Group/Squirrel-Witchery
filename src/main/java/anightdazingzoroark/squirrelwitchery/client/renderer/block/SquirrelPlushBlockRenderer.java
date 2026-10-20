package anightdazingzoroark.squirrelwitchery.client.renderer.block;

import anightdazingzoroark.riftlib.block.AnimatedBlockStateHolder;
import anightdazingzoroark.riftlib.core.manager.AnimationDataBlock;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoBlockRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.blocks.SquirrelWitcheryBlocks;
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
                        if (animatedBlockStateHolder.getBlockState().getBlock() == SquirrelWitcheryBlocks.WITCH_SQUIRREL_PLUSH) {
                            return "geometry.witch_squirrel_plush";
                        }
                        return "geometry.squirrel_plush";
                    }

                    @Override
                    @NotNull
                    public String getTextureLocation(AnimatedBlockStateHolder animatedBlockStateHolder) {
                        if (animatedBlockStateHolder.getBlockState().getBlock() == SquirrelWitcheryBlocks.WITCH_SQUIRREL_PLUSH) {
                            return "blocks/witch_squirrel_plush.png";
                        }
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
