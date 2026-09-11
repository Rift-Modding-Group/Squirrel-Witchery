package anightdazingzoroark.squirrelwitchery.client;

import anightdazingzoroark.squirrelwitchery.server.items.WitchShotgunItem;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents {
    @SubscribeEvent
    public void poseWitchShotgun(RenderLivingEvent.Pre<?> event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;
        if (!(event.getRenderer() instanceof RenderPlayer renderer)) return;

        EnumHandSide shotgunSide;
        if (player.getHeldItemMainhand().getItem() instanceof WitchShotgunItem) {
            shotgunSide = player.getPrimaryHand();
        }
        else if (player.getHeldItemOffhand().getItem() instanceof WitchShotgunItem) {
            shotgunSide = player.getPrimaryHand().opposite();
        }
        else return;

        ModelPlayer model = renderer.getMainModel();
        if (shotgunSide == EnumHandSide.RIGHT) model.rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
        else model.leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
    }
}
