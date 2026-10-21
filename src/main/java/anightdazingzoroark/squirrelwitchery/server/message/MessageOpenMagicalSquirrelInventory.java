package anightdazingzoroark.squirrelwitchery.server.message;

import anightdazingzoroark.riftlib.message.RiftLibMessage;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import anightdazingzoroark.squirrelwitchery.server.gui.SquirrelWitcheryGuiHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

public class MessageOpenMagicalSquirrelInventory extends RiftLibMessage<MessageOpenMagicalSquirrelInventory> {
    private int entityId;

    public MessageOpenMagicalSquirrelInventory() {}

    public MessageOpenMagicalSquirrelInventory(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void fromBytes(@NotNull ByteBuf buffer) {
        this.entityId = buffer.readInt();
    }

    @Override
    public void toBytes(@NotNull ByteBuf buffer) {
        buffer.writeInt(this.entityId);
    }

    @Override
    public void executeOnServer(
            @NotNull MinecraftServer server, @NotNull MessageOpenMagicalSquirrelInventory message,
            @NotNull EntityPlayer player, @NotNull MessageContext context
    ) {
        if (!(player.world.getEntityByID(message.entityId) instanceof MagicalSquirrelEntity squirrel)) return;
        if (!squirrel.getInventoryHandler().isUsableByPlayer(player)) return;
        player.openGui(
                SquirrelWitchery.instance, SquirrelWitcheryGuiHandler.MAGICAL_SQUIRREL_INVENTORY,
                player.world, message.entityId, 0, 0
        );
    }

    @Override
    public void executeOnClient(
            @NotNull Minecraft minecraft, @NotNull MessageOpenMagicalSquirrelInventory message,
            @NotNull EntityPlayer player, @NotNull MessageContext context
    ) {}
}
