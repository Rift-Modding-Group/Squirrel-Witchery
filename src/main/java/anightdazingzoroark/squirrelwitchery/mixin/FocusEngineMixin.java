package anightdazingzoroark.squirrelwitchery.mixin;

import anightdazingzoroark.squirrelwitchery.server.items.WitchStaffAttachmentItem;
import anightdazingzoroark.squirrelwitchery.server.items.WitchStaffItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.IFocusElement;

import java.util.ArrayList;

@Mixin(value = FocusEngine.class, remap = false)
public abstract class FocusEngineMixin {
    @Redirect(
            method = "castFocusPackage(Lnet/minecraft/entity/EntityLivingBase;Lthaumcraft/api/casters/FocusPackage;Z)V",
            at = @At(value = "INVOKE", target = "Lthaumcraft/api/casters/FocusPackage;initialize(Lnet/minecraft/entity/EntityLivingBase;)V"),
            require = 1
    )
    private static void applyWitchStaffAttachments(FocusPackage focusPackage, EntityLivingBase caster) {
        focusPackage.initialize(caster);
        if (caster.world.isRemote || !(caster instanceof EntityPlayer player)) return;

        ItemStack staffStack = player.getHeldItemMainhand();
        if (!(staffStack.getItem() instanceof WitchStaffItem)
                || !staffStack.hasTagCompound()
                || !staffStack.getTagCompound().getBoolean("Casting")
        ) {
            staffStack = player.getHeldItemOffhand();
        }
        if (!(staffStack.getItem() instanceof WitchStaffItem staff)
                || !staffStack.hasTagCompound()
                || !staffStack.getTagCompound().getBoolean("Casting")
        ) return;

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

        int risunium = staff.getRisuniumAmount(staffStack);
        for (WitchStaffAttachmentItem.Type type : WitchStaffAttachmentItem.Type.values()) {
            if (!staff.hasAttachment(staffStack, type) || risunium < WitchStaffItem.ATTACHMENT_RISUNIUM_COST) continue;
            if (type.applySpellModification(elements, packages)) {
                risunium -= WitchStaffItem.ATTACHMENT_RISUNIUM_COST;
            }
        }

        staff.setRisuniumAmount(staffStack, risunium);
        player.inventoryContainer.detectAndSendChanges();
    }
}
