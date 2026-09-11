package anightdazingzoroark.squirrelwitchery.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thaumcraft.api.casters.NodeSetting;

@Mixin(value = NodeSetting.class, remap = false)
public interface NodeSettingAccessor {
    @Accessor("value")
    public void squirrelWitchery$setValue(int value);
}
