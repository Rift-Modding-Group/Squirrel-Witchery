package anightdazingzoroark.squirrelwitchery.client.model;

import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.armor.WitchCostumeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WitchCostumeModel extends AnimatedGeoModel<WitchCostumeHolder> {
    @Override
    @NotNull
    public String getModId() {
        return SquirrelWitchery.MODID;
    }

    @Override
    public String getModelIdentifier(WitchCostumeHolder witchCostumeHolder) {
        return "geometry.witch_costume";
    }

    @Override
    public String getTextureLocation(WitchCostumeHolder witchCostumeHolder) {
        return "armor/witch_costume.png";
    }

    @Override
    @NotNull
    public List<String> getAnimationIdentifiers(WitchCostumeHolder witchCostumeHolder) {
        return List.of();
    }
}
