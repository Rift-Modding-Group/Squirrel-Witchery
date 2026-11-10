package anightdazingzoroark.squirrelwitchery.mixin;

import anightdazingzoroark.squirrelwitchery.SquirrelWitcheryConfig;
import anightdazingzoroark.squirrelwitchery.server.items.WitchStaffAttachmentItem;
import anightdazingzoroark.squirrelwitchery.server.items.WitchStaffItem;
import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.items.casters.foci.FocusEffectHeal;

import java.util.ArrayList;

@Mixin(value = FocusEngine.class, remap = false)
public abstract class FocusEngineMixin {
    @Redirect(
            method = "runFocusPackage(Lthaumcraft/api/casters/FocusPackage;[Lthaumcraft/api/casters/Trajectory;[Lnet/minecraft/util/math/RayTraceResult;)V",
            at = @At(value = "INVOKE", target = "Lthaumcraft/api/casters/FocusEffect;execute(Lnet/minecraft/util/math/RayTraceResult;Lthaumcraft/api/casters/Trajectory;FI)Z"),
            require = 1
    )
    private static boolean preventFriendlySpellEffects(
            @NotNull FocusEffect effect, @Nullable RayTraceResult target, @Nullable Trajectory trajectory, float power, int num
    ) {
        if (SquirrelWitcheryConfig.magicalSquirrelBehavior.noFriendlyFire
                && target != null && target.entityHit instanceof MagicalSquirrelEntity squirrel
                && squirrel.isTamed() && squirrel.getTransformed()
                && squirrel.isRelatedToEntity(effect.getPackage().getCaster())
                && !(effect instanceof FocusEffectHeal)
        ) {
            return false;
        }
        return effect.execute(target, trajectory, power, num);
    }

    @Redirect(
            method = "castFocusPackage(Lnet/minecraft/entity/EntityLivingBase;Lthaumcraft/api/casters/FocusPackage;Z)V",
            at = @At(value = "INVOKE", target = "Lthaumcraft/api/casters/FocusPackage;initialize(Lnet/minecraft/entity/EntityLivingBase;)V"),
            require = 1
    )
    private static void applyWitchStaffAttachments(FocusPackage focusPackage, EntityLivingBase caster) {
        focusPackage.initialize(caster);
        if (caster.world.isRemote || !(caster instanceof EntityPlayer || caster instanceof MagicalSquirrelEntity)) return;

        //get staff
        ItemStack staffStack = getCastingWitchStaff(caster, EnumHand.MAIN_HAND);
        if (staffStack.isEmpty()) staffStack = getCastingWitchStaff(caster, EnumHand.OFF_HAND);
        if (staffStack.isEmpty()) return;

        //prepare spell to be modified
        ArrayList<IFocusElement> elements = new ArrayList<>();
        ArrayList<FocusPackage> packages = new ArrayList<>();
        elements.add(focusPackage);
        for (int index = 0; index < elements.size(); index++) {
            IFocusElement element = elements.get(index);
            if (element instanceof FocusPackage nestedPackage) {
                packages.add(nestedPackage);
                elements.addAll(nestedPackage.nodes);
            }
            if (element instanceof FocusModSplit split) elements.addAll(split.getSplitPackages());
        }

        //apply staff attachment effects
        WitchStaffItem staff = (WitchStaffItem) staffStack.getItem();
        int risunium = staff.getRisuniumAmount(staffStack);
        WitchStaffAttachmentItem.Type attachment = staff.getAttachment(staffStack);
        if (attachment != null
                && risunium >= WitchStaffItem.ATTACHMENT_RISUNIUM_COST
                && attachment.applySpellModification(elements, packages)
        ) {
            risunium -= WitchStaffItem.ATTACHMENT_RISUNIUM_COST;
        }

        //update staff
        staff.setRisuniumAmount(staffStack, risunium);
        if (caster instanceof EntityPlayer player) player.inventoryContainer.detectAndSendChanges();
        else {
            MagicalSquirrelEntity squirrel = (MagicalSquirrelEntity) caster;
            squirrel.markEquipmentDirty();
        }
    }

    @Unique
    @NotNull
    private static ItemStack getCastingWitchStaff(@NotNull EntityLivingBase caster, @NotNull EnumHand hand) {
        ItemStack staffStack = caster.getHeldItem(hand);
        if (!(staffStack.getItem() instanceof WitchStaffItem)) return ItemStack.EMPTY;

        if (caster instanceof MagicalSquirrelEntity || (staffStack.hasTagCompound() && staffStack.getTagCompound().getBoolean("Casting"))) {
            return staffStack;
        }
        return ItemStack.EMPTY;
    }
}
