package anightdazingzoroark.squirrelwitchery.client.renderer.entity;

import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoEntityLivingRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import org.jetbrains.annotations.NotNull;

public class MagicalSquirrelEntityRenderer extends GeoEntityLivingRenderer<MagicalSquirrelEntity> {
    public MagicalSquirrelEntityRenderer(@NotNull RenderManager renderManager) {
        super(
                renderManager,
                new AnimatedGeoModel<MagicalSquirrelEntity>() {
                    @Override
                    @NotNull
                    public String getModId() {
                        return SquirrelWitchery.MODID;
                    }

                    @Override
                    @NotNull
                    public String getModelIdentifier(MagicalSquirrelEntity magicalSquirrelEntity) {
                        return "geometry.magical_squirrel";
                    }

                    @Override
                    @NotNull
                    public String getTextureLocation(MagicalSquirrelEntity magicalSquirrelEntity) {
                        return "entity/magical_squirrel.png";
                    }
                }
        );
    }
}