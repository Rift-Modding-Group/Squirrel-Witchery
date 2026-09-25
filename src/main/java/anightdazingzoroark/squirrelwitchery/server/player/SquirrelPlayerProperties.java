package anightdazingzoroark.squirrelwitchery.server.player;

import anightdazingzoroark.riftlib.nbtStorageUser.propertySystem.AbstractEntityProperties;
import anightdazingzoroark.riftlib.nbtStorageUser.propertySystem.RiftLibProperty;
import anightdazingzoroark.riftlib.nbtStorageUser.propertyValue.BooleanPropertyValue;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SquirrelPlayerProperties extends AbstractEntityProperties<EntityPlayer> {
    public SquirrelPlayerProperties(@NotNull String propertyName, @NotNull EntityPlayer entityHolder) {
        super(propertyName, entityHolder);
    }

    @Nullable
    public static SquirrelPlayerProperties get(@Nullable EntityPlayer player) {
        if (player == null) return null;
        return RiftLibProperty.getProperty(SquirrelWitchery.MODID + ":player", player);
    }

    @Override
    protected void registerDefaults(EntityPlayer player) {
        this.register(new BooleanPropertyValue("UsedHeart", false));
    }

    public boolean hasSquirrelAttachments() {
        return this.get("UsedHeart");
    }
}
