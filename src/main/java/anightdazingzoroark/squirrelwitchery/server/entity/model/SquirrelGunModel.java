package anightdazingzoroark.squirrelwitchery.server.entity.model;

import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.entity.SquirrelGunEntity;
import org.jetbrains.annotations.NotNull;

public class SquirrelGunModel extends AnimatedGeoModel<SquirrelGunEntity> {
    @Override
    @NotNull
    public String getModId() {
        return SquirrelWitchery.MODID;
    }

    @Override
    @NotNull
    public String getModelIdentifier(@NotNull SquirrelGunEntity squirrel) {
        return "geometry.squirrel_with_gun";
    }

    @Override
    @NotNull
    public String getTextureLocation(@NotNull SquirrelGunEntity squirrel) {
        return "entity/squirrel_with_gun.png";
    }
}