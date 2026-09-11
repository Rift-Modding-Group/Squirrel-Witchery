package anightdazingzoroark.squirrelwitchery.server.items;

import anightdazingzoroark.squirrelwitchery.mixin.NodeSettingAccessor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.common.items.casters.foci.FocusMediumProjectile;

import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;

public class WitchStaffAttachmentItem extends Item {
    @NotNull
    private final Type type;

    public WitchStaffAttachmentItem(@NotNull Type type) {
        super();
        this.type = type;
    }

    @NotNull
    public Type getType() {
        return this.type;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        tooltip.add(TextFormatting.GRAY + I18n.format(
                "witch_staff.attachment.tooltip." + this.type.name().toLowerCase(Locale.ROOT)
        ));
        tooltip.add(TextFormatting.DARK_PURPLE + I18n.format(
                "witch_staff.attachment.tooltip.cost", WitchStaffItem.ATTACHMENT_RISUNIUM_COST
        ));
    }

    public enum Type {
        POWER_BOOSTER((elements, packages) -> {
            boolean hasEffect = false;
            for (IFocusElement element : elements) {
                if (element instanceof FocusEffect) {
                    hasEffect = true;
                    break;
                }
            }
            if (!hasEffect) return false;

            for (FocusPackage focusPackage : packages) focusPackage.multiplyPower(1.25f);
            return true;
        }),
        LINGERER((elements, packages) -> {
            boolean changedDuration = false;
            for (IFocusElement element : elements) {
                if (!(element instanceof FocusNode node) || !node.getSettingList().contains("duration")) continue;
                NodeSetting duration = node.getSetting("duration");
                int boostedDuration = Math.max(
                        duration.getValue() + 1,
                        (int) Math.ceil(duration.getValue() * 1.5f)
                );
                ((NodeSettingAccessor) (Object) duration).squirrelWitchery$setValue(boostedDuration);
                changedDuration = true;
            }
            return changedDuration;
        }),
        PROJECTILE_BOOSTER((elements, packages) -> {
            boolean changedSpeed = false;
            for (IFocusElement element : elements) {
                if (!(element instanceof FocusMediumProjectile projectile)) continue;
                NodeSetting speed = projectile.getSetting("speed");
                int boostedSpeed = Math.max(speed.getValue() + 1, (int) Math.ceil(speed.getValue() * 1.5f));
                ((NodeSettingAccessor) (Object) speed).squirrelWitchery$setValue(boostedSpeed);
                changedSpeed = true;
            }
            return changedSpeed;
        });

        @NotNull
        private final BiFunction<List<IFocusElement>, List<FocusPackage>, Boolean> spellModification;

        Type(@NotNull BiFunction<List<IFocusElement>, List<FocusPackage>, Boolean> spellModification) {
            this.spellModification = spellModification;
        }

        public boolean applySpellModification(List<IFocusElement> elements, List<FocusPackage> packages) {
            return this.spellModification.apply(elements, packages);
        }
    }
}
