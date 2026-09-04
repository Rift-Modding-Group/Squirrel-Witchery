package anightdazingzoroark.squirrelwitchery.client.renderer;

import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoEntityRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.entity.SquirrelEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import org.jetbrains.annotations.NotNull;

public class SquirrelEntityRenderer extends GeoEntityRenderer<SquirrelEntity> {
    public SquirrelEntityRenderer(RenderManager renderManager) {
        super(renderManager, new AnimatedGeoModel<SquirrelEntity>() {
            @Override
            @NotNull
            public String getModId() {
                return SquirrelWitchery.MODID;
            }

            @Override
            public String getModelIdentifier(SquirrelEntity squirrelEntity) {
                return "geometry.squirrel";
            }

            @Override
            public String getTextureLocation(SquirrelEntity squirrelEntity) {
                if (squirrelEntity.isSheared()) return "entity/squirrel_sheared.png";
                return "entity/squirrel.png";
            }
        });
    }
}
