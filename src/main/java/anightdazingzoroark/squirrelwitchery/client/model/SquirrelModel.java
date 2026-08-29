package anightdazingzoroark.squirrelwitchery.client.model;

import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.entity.SquirrelEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SquirrelModel extends AnimatedGeoModel<SquirrelEntity> {
    @Override
    public @NotNull String getModId() {
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

    @Override
    @NotNull
    public List<String> getAnimationIdentifiers(SquirrelEntity squirrelEntity) {
        return List.of("animation.squirrel.walk", "animation.squirrel.sheared");
    }
}
