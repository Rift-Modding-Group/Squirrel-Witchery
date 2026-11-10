package anightdazingzoroark.squirrelwitchery.mixin;

import anightdazingzoroark.squirrelwitchery.server.casters.BoostedNodeSettingRange;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.NodeSetting;

/**
 * should help out in going beyond thaumcraft's imposed limits on some spell traits
 * for the witchs staff attachments
 * */
@Mixin(value = FocusPackage.class, remap = false)
public abstract class FocusPackageMixin {
    @WrapOperation(
            method = "serialize",
            at = @At(value = "INVOKE", target = "Lthaumcraft/api/casters/FocusNode;getSettingValue(Ljava/lang/String;)I"),
            require = 1
    )
    private int serializeBoostedSetting(
            @NotNull FocusNode node, @NotNull String key, @NotNull Operation<Integer> original,
            @Local(ordinal = 1) @NotNull NBTTagCompound nodeTag
    ) {
        if (node.getSetting(key).getType() instanceof BoostedNodeSettingRange) {
            nodeTag.setBoolean("squirrelwitchery.boosted." + key, true);
        }
        return original.call(node, key);
    }

    @WrapOperation(
            method = "deserialize",
            at = @At(value = "INVOKE", target = "Lthaumcraft/api/casters/NodeSetting;setValue(I)V"),
            require = 1
    )
    private void deserializeBoostedSetting(
            @NotNull NodeSetting setting, int value, @NotNull Operation<Void> original,
            @Local(ordinal = 1) @NotNull NBTTagCompound nodeTag
    ) {
        if (nodeTag.getBoolean("squirrelwitchery.boosted." + setting.key)) {
            BoostedNodeSettingRange.setBoostedValue(setting, value);
        }
        else {
            original.call(setting, value);
        }
    }
}
