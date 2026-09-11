package anightdazingzoroark.squirrelwitchery.server;

import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import anightdazingzoroark.squirrelwitchery.server.items.WitchStaffAttachmentItem;
import anightdazingzoroark.squirrelwitchery.server.items.WitchStaffItem;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.IArcaneWorkbench;

public class WitchStaffAttachmentRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IArcaneRecipe {
    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        if (!(inventory instanceof IArcaneWorkbench)) return false;

        int staffCount = 0;
        int attachmentCount = 0;
        for (int slot = 0; slot < Math.min(9, inventory.getSizeInventory()); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof WitchStaffItem) staffCount++;
            else if (stack.getItem() instanceof WitchStaffAttachmentItem) attachmentCount++;
            else return false;
        }
        if (staffCount != 1 || attachmentCount > 1) return false;

        ItemStack staffStack = this.findStaff(inventory);
        WitchStaffAttachmentItem.Type addedAttachment = this.findAddedAttachment(inventory);
        WitchStaffItem staff = (WitchStaffItem) staffStack.getItem();
        if (addedAttachment != null) return staff.getAttachment(staffStack) == null;
        return this.findRemovedAttachment(inventory) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack staffStack = this.findStaff(inventory);
        if (staffStack.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = staffStack.copy();
        result.setCount(1);
        WitchStaffAttachmentItem.Type addedAttachment = this.findAddedAttachment(inventory);
        WitchStaffAttachmentItem.Type changedAttachment = addedAttachment != null
                ? addedAttachment
                : this.findRemovedAttachment(inventory);
        if (changedAttachment == null) return ItemStack.EMPTY;

        ((WitchStaffItem) result.getItem()).setAttachment(result, addedAttachment);
        return result;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(SquirrelWitcheryItems.WITCH_STAFF);
    }

    @Override
    public int getVis() {
        return 25;
    }

    @Override
    public String getResearch() {
        return SquirrelWitcheryResearch.WITCH_STAFF_ATTACHMENTS;
    }

    @Override
    public AspectList getCrystals() {
        return new AspectList();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventory) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);
        if (this.findAddedAttachment(inventory) != null) return remaining;

        WitchStaffAttachmentItem.Type removedAttachment = this.findRemovedAttachment(inventory);
        if (removedAttachment == null) return remaining;

        Item attachmentItem = switch (removedAttachment) {
            case POWER_BOOSTER -> SquirrelWitcheryItems.RISUNIC_FOCAL_POWER_BOOSTER;
            case LINGERER -> SquirrelWitcheryItems.RISUNIC_FOCAL_LINGERER;
            case PROJECTILE_BOOSTER -> SquirrelWitcheryItems.RISUNIC_FOCAL_PROJECTILE_BOOSTER;
        };
        for (int slot = 0; slot < Math.min(9, inventory.getSizeInventory()); slot++) {
            if (inventory.getStackInSlot(slot).getItem() instanceof WitchStaffItem) {
                remaining.set(slot, new ItemStack(attachmentItem));
                break;
            }
        }
        return remaining;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    private ItemStack findStaff(InventoryCrafting inventory) {
        for (int slot = 0; slot < Math.min(9, inventory.getSizeInventory()); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.getItem() instanceof WitchStaffItem) return stack;
        }
        return ItemStack.EMPTY;
    }

    private WitchStaffAttachmentItem.Type findAddedAttachment(InventoryCrafting inventory) {
        for (int slot = 0; slot < Math.min(9, inventory.getSizeInventory()); slot++) {
            if (inventory.getStackInSlot(slot).getItem() instanceof WitchStaffAttachmentItem attachment) {
                return attachment.getType();
            }
        }
        return null;
    }

    private WitchStaffAttachmentItem.Type findRemovedAttachment(InventoryCrafting inventory) {
        ItemStack staffStack = this.findStaff(inventory);
        if (staffStack.isEmpty() || this.findAddedAttachment(inventory) != null) return null;

        WitchStaffItem staff = (WitchStaffItem) staffStack.getItem();
        return staff.getAttachment(staffStack);
    }
}
