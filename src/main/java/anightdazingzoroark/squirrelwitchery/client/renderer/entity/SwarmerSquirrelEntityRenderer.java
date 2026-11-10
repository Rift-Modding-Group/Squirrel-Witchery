package anightdazingzoroark.squirrelwitchery.client.renderer.entity;

import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoEntityLivingRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.entity.SwarmerSquirrelEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import org.jetbrains.annotations.NotNull;

public class SwarmerSquirrelEntityRenderer extends GeoEntityLivingRenderer<SwarmerSquirrelEntity> {
    public SwarmerSquirrelEntityRenderer(@NotNull RenderManager renderManager) {
        super(renderManager, new AnimatedGeoModel<SwarmerSquirrelEntity>() {
            @Override
            @NotNull
            public String getModId() {
                return SquirrelWitchery.MODID;
            }

            @Override
            @NotNull
            public String getModelIdentifier(SwarmerSquirrelEntity swarmerSquirrelEntity) {
                return "geometry.squirrel";
            }

            @Override
            @NotNull
            public String getTextureLocation(SwarmerSquirrelEntity swarmerSquirrelEntity) {
                return "entity/swarmer_squirrel.png";
            }
        });
    }
}