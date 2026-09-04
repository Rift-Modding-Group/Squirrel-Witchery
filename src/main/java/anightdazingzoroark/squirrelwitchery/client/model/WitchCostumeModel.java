package anightdazingzoroark.squirrelwitchery.client.model;

import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.armor.WitchCostumeHolder;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.item.Item;
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
        Item item = witchCostumeHolder.getAnimationData().getStack().getItem();
        if (item == SquirrelWitcheryItems.DARK_WITCH_HAT || item == SquirrelWitcheryItems.DARK_WITCH_ROBE
                || item == SquirrelWitcheryItems.DARK_WITCH_SKIRT || item == SquirrelWitcheryItems.DARK_WITCH_BOOTS
        ) {
            return "armor/dark_witch_costume.png";
        }
        return "armor/witch_costume.png";
    }
}