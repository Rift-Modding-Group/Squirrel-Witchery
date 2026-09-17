package anightdazingzoroark.squirrelwitchery.client;

import anightdazingzoroark.squirrelwitchery.server.items.NutsaberItem;
import anightdazingzoroark.squirrelwitchery.server.items.WitchShotgunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientEvents {
    private int selectedHotbarSlot = -1;

    //make sure that nutsaber name doesn't reappear constantly
    //when reducing risunium amount
    @SubscribeEvent
    public void preventNutsaberRisuniumHighlight(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null) {
            this.selectedHotbarSlot = -1;
            return;
        }

        int currentHotbarSlot = minecraft.player.inventory.currentItem;
        if (currentHotbarSlot != this.selectedHotbarSlot) {
            this.selectedHotbarSlot = currentHotbarSlot;
            return;
        }

        ItemStack currentStack = minecraft.player.inventory.getCurrentItem();
        ItemStack highlightedStack = minecraft.ingameGUI.highlightingItemStack;
        if (currentStack.getItem() instanceof NutsaberItem && highlightedStack.getItem() instanceof NutsaberItem && currentStack != highlightedStack) {
            minecraft.ingameGUI.highlightingItemStack = currentStack;
        }
    }

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
