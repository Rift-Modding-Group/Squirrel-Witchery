package anightdazingzoroark.squirrelwitchery.client.renderer;

import anightdazingzoroark.riftlib.renderers.geo.GeoEntityRenderer;
import anightdazingzoroark.squirrelwitchery.client.model.SquirrelModel;
import anightdazingzoroark.squirrelwitchery.server.entity.SquirrelEntity;
import net.minecraft.client.renderer.entity.RenderManager;

public class SquirrelRenderer extends GeoEntityRenderer<SquirrelEntity> {
    public SquirrelRenderer(RenderManager renderManager) {
        super(renderManager, new SquirrelModel());
    }
}
