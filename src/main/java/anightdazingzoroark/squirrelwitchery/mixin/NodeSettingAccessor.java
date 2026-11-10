package anightdazingzoroark.squirrelwitchery.mixin;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thaumcraft.api.casters.NodeSetting;

@Mixin(value = NodeSetting.class, remap = false)
public interface NodeSettingAccessor {
    @Accessor("value")
    void squirrelWitchery$setValue(int value);

    @Accessor("type")
    void squirrelWitchery$setType(@NotNull NodeSetting.INodeSettingType type);
}
