package anightdazingzoroark.squirrelwitchery.client.renderer.entity;

import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoEntityNonLivingRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.entity.NutRainEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

public class NutRainEntityRenderer extends GeoEntityNonLivingRenderer<NutRainEntity> {
    public NutRainEntityRenderer(@NotNull RenderManager renderManager) {
        super(renderManager, new AnimatedGeoModel<NutRainEntity>() {
            @Override
            @NotNull
            public String getModId() {
                return SquirrelWitchery.MODID;
            }

            @Override
            @NotNull
            public String getModelIdentifier(@NotNull NutRainEntity entity) {
                return "geometry.blank_entity";
            }

            @Override
            @NotNull
            public String getTextureLocation(@NotNull NutRainEntity entity) {
                return "entity/blank_entity.png";
            }
        });
    }

    @Override
    public boolean shouldRender(@NotNull NutRainEntity entity, @NotNull ICamera camera, double camX, double camY, double camZ) {
        return super.shouldRender(entity, camera, camX, camY, camZ) || camera.isBoundingBoxInFrustum(entity.getRainArea());
    }
}
