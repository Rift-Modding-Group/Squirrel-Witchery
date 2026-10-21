package anightdazingzoroark.squirrelwitchery.server.gui;

import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SquirrelWitcheryGuiHandler implements IGuiHandler {
    public static final int MAGICAL_SQUIRREL_INVENTORY = 0;

    @Override
    @Nullable
    public Object getServerGuiElement(int id, @NotNull EntityPlayer player, @NotNull World world, int x, int y, int z) {
        if (id != MAGICAL_SQUIRREL_INVENTORY) return null;
        if (!(world.getEntityByID(x) instanceof MagicalSquirrelEntity squirrel)) return null;
        if (!squirrel.getInventoryHandler().isUsableByPlayer(player)) return null;
        return new ContainerDispenser(player.inventory, squirrel.getInventoryHandler());
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Nullable
    public Object getClientGuiElement(int id, @NotNull EntityPlayer player, @NotNull World world, int x, int y, int z) {
        if (id != MAGICAL_SQUIRREL_INVENTORY) return null;
        if (!(world.getEntityByID(x) instanceof MagicalSquirrelEntity squirrel)) return null;
        return new GuiDispenser(player.inventory, squirrel.getInventoryHandler());
    }
}
