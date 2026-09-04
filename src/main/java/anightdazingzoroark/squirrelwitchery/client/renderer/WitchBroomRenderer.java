package anightdazingzoroark.squirrelwitchery.client.renderer;

import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoEntityRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.entity.WitchBroomEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import org.jetbrains.annotations.NotNull;

public class WitchBroomRenderer extends GeoEntityRenderer<WitchBroomEntity> {
    public WitchBroomRenderer(RenderManager renderManager) {
        super(renderManager, new AnimatedGeoModel<WitchBroomEntity>() {
            @Override
            @NotNull
            public String getModId() {
                return SquirrelWitchery.MODID;
            }

            @Override
            public String getModelIdentifier(WitchBroomEntity witchBroomEntity) {
                return "geometry.witch_broom";
            }

            @Override
            public String getTextureLocation(WitchBroomEntity witchBroomEntity) {
                return "entity/witch_broom.png";
            }
        });
    }
}
