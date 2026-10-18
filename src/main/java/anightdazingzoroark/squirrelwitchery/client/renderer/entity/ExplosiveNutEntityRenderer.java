package anightdazingzoroark.squirrelwitchery.client.renderer.entity;

import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoProjectileRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.entity.ExplosiveNutProjectileEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import org.jetbrains.annotations.NotNull;

public class ExplosiveNutEntityRenderer extends GeoProjectileRenderer<ExplosiveNutProjectileEntity> {
    public ExplosiveNutEntityRenderer(RenderManager renderManager) {
        super(renderManager, new AnimatedGeoModel<ExplosiveNutProjectileEntity>() {
            @Override
            @NotNull
            public String getModId() {
                return SquirrelWitchery.MODID;
            }

            @Override
            @NotNull
            public String getModelIdentifier(ExplosiveNutProjectileEntity explosiveNutProjectileEntity) {
                return "geometry.explosive_nut";
            }

            @Override
            @NotNull
            public String getTextureLocation(ExplosiveNutProjectileEntity explosiveNutProjectileEntity) {
                return "entity/explosive_nut.png";
            }
        });
    }
}
