package anightdazingzoroark.squirrelwitchery.client.renderer.entity;

import anightdazingzoroark.riftlib.renderers.geo.GeoEntityLivingRenderer;
import anightdazingzoroark.squirrelwitchery.server.entity.SquirrelGunEntity;
import anightdazingzoroark.squirrelwitchery.server.entity.model.SquirrelGunModel;
import net.minecraft.client.renderer.entity.RenderManager;

public class SquirrelGunEntityRenderer extends GeoEntityLivingRenderer<SquirrelGunEntity> {
    public SquirrelGunEntityRenderer(RenderManager renderManager) {
        super(renderManager, new SquirrelGunModel());
    }
}