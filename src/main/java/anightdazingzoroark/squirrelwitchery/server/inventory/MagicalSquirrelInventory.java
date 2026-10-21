package anightdazingzoroark.squirrelwitchery.server.inventory;

import anightdazingzoroark.riftlib.inventory.RiftLibInventoryHandler;
import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

public class MagicalSquirrelInventory extends RiftLibInventoryHandler implements IInventory {
    @NotNull
    private final MagicalSquirrelEntity squirrel;

    public MagicalSquirrelInventory(@NotNull MagicalSquirrelEntity squirrel) {
        super(9);
        this.squirrel = squirrel;
    }

    @Override
    public int getSizeInventory() {
        return this.getSlots();
    }

    @Override
    @NotNull
    public ItemStack decrStackSize(int index, int count) {
        return this.extractItem(index, count, false);
    }

    @Override
    @NotNull
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = this.getStackInSlot(index);
        this.setStackInSlot(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, @NotNull ItemStack stack) {
        if (stack.getCount() > this.getInventoryStackLimit()) stack.setCount(this.getInventoryStackLimit());
        this.setStackInSlot(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean isUsableByPlayer(@NotNull EntityPlayer player) {
        return this.squirrel.isEntityAlive()
                && this.squirrel.world == player.world
                && this.squirrel.isTamed()
                && this.squirrel.getTransformed()
                && this.squirrel.isOwner(player)
                && this.squirrel.getDistanceSq(player) <= 64D;
    }

    @Override
    public void openInventory(@NotNull EntityPlayer player) {}

    @Override
    public void closeInventory(@NotNull EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, @NotNull ItemStack stack) {
        return this.isItemValid(index, stack);
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int index = 0; index < this.getSlots(); index++) {
            this.setStackInSlot(index, ItemStack.EMPTY);
        }
    }

    @Override
    @NotNull
    public String getName() {
        return this.squirrel.getName();
    }

    @Override
    public boolean hasCustomName() {
        return this.squirrel.hasCustomName();
    }

    @Override
    @NotNull
    public ITextComponent getDisplayName() {
        return this.squirrel.getDisplayName();
    }
}