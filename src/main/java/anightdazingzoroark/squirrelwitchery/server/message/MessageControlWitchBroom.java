package anightdazingzoroark.squirrelwitchery.server.message;

import anightdazingzoroark.riftlib.message.RiftLibMessage;
import anightdazingzoroark.squirrelwitchery.server.entity.WitchBroomEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageControlWitchBroom extends RiftLibMessage<MessageControlWitchBroom> {
    private int entityId;
    private WitchBroomEntity.FlightState flightState;

    public MessageControlWitchBroom() {}

    public MessageControlWitchBroom(WitchBroomEntity witchBroomEntity, WitchBroomEntity.FlightState flightState) {
        this.entityId = witchBroomEntity.getEntityId();
        this.flightState = flightState;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.flightState = WitchBroomEntity.FlightState.values()[buf.readByte()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeByte(this.flightState.ordinal());
    }

    @Override
    public void executeOnServer(MinecraftServer server, MessageControlWitchBroom message, EntityPlayer entityPlayer, MessageContext context) {
        WitchBroomEntity witchBroomEntity = (WitchBroomEntity) server.getEntityWorld().getEntityByID(message.entityId);
        if (witchBroomEntity == null) return;

        if (message.flightState == WitchBroomEntity.FlightState.UP) {
            witchBroomEntity.setGoingUp(true);
            witchBroomEntity.setGoingDown(false);
        }
        else if (message.flightState == WitchBroomEntity.FlightState.DOWN) {
            witchBroomEntity.setGoingUp(false);
            witchBroomEntity.setGoingDown(true);
        }
        else {
            witchBroomEntity.setGoingUp(false);
            witchBroomEntity.setGoingDown(false);
        }
    }

    @Override
    public void executeOnClient(Minecraft minecraft, MessageControlWitchBroom message, EntityPlayer entityPlayer, MessageContext context) {}
}
