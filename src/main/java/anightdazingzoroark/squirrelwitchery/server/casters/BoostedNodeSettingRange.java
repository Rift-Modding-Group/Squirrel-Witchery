package anightdazingzoroark.squirrelwitchery.server.casters;

import anightdazingzoroark.squirrelwitchery.mixin.NodeSettingAccessor;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.casters.NodeSetting;

/**
 * A cast-time range that also identifies boosted settings when a spell is serialized.
 * */
public class BoostedNodeSettingRange extends NodeSetting.NodeSettingIntRange {
    public BoostedNodeSettingRange() {
        super(0, Integer.MAX_VALUE);
    }

    public static void setBoostedValue(@NotNull NodeSetting setting, int value) {
        NodeSettingAccessor accessor = (NodeSettingAccessor) (Object) setting;
        accessor.squirrelWitchery$setType(new BoostedNodeSettingRange());
        accessor.squirrelWitchery$setValue(value);
    }
}
