package anightdazingzoroark.squirrelwitchery.client;

import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.entity.WitchBroomEntity;
import anightdazingzoroark.squirrelwitchery.server.items.IRisuniumConsumer;
import anightdazingzoroark.squirrelwitchery.server.items.WitchBroomItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.casters.ICaster;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ModConfig;

public class SquirrelWitcheryOverlay {
    private static final ResourceLocation HUD_TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/hud.png");

    @SubscribeEvent
    public void renderRisuniumAmount(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.player;
        if (player == null || minecraft.gameSettings.hideGUI) return;

        int[] amountAndMax = this.getRisuniumAmntAndMax(player);
        int amount = amountAndMax[0];
        int maximum = amountAndMax[1];
        if (maximum <= 0) return;

        int risuniumColor = SquirrelWitcheryAspects.RISUNIUM.getColor();

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GlStateManager.disableLighting();
        GlStateManager.color(1f, 1f, 1f, 1f);

        //---position this hud element---
        boolean holdingCaster = player.getHeldItemMainhand().getItem() instanceof ICaster || player.getHeldItemOffhand().getItem() instanceof ICaster;
        GlStateManager.translate(
                holdingCaster ? 40 : 0,
                ModConfig.CONFIG_GRAPHICS.dialBottom ? event.getResolution().getScaledHeight() - 32 : 0,
                0f
        );

        minecraft.getTextureManager().bindTexture(HUD_TEXTURE);

        //-----ring-----
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5D, 0.5D, 0.5D);
        UtilsFX.drawTexturedQuad(0f, 0f, 0f, 0f, 64f, 64f, -90D);
        GlStateManager.popMatrix();

        //-----risunium icon----
        UtilsFX.drawTag(8, 8, SquirrelWitcheryAspects.RISUNIUM);

        //-----risunium meter-----
        minecraft.getTextureManager().bindTexture(HUD_TEXTURE);
        GlStateManager.translate(16f, 16f, 0f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(16f, -10f, 0f);
        GlStateManager.scale(0.5D, 0.5D, 0.5D);

        //---risunium fill---
        int filledHeight = Math.clamp((int) (30f * amount / maximum), 0, 30);
        if (filledHeight > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.color(
                    (risuniumColor >> 16 & 0xFF) / 255f,
                    (risuniumColor >> 8 & 0xFF) / 255f,
                    (risuniumColor & 0xFF) / 255f,
                    0.8f
            );
            UtilsFX.drawTexturedQuad(-4f, 35f - filledHeight, 104f, 0f, 8f, filledHeight, -90D);
            GlStateManager.popMatrix();
        }

        //---meter container---
        GlStateManager.color(1f, 1f, 1f, 1f);
        UtilsFX.drawTexturedQuad(-8f, -3f, 72f, 0f, 16f, 42f, -90D);

        GlStateManager.popMatrix();

        GlStateManager.color(1f, 1f, 1f, 1f);

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private int[] getRisuniumAmntAndMax(@NotNull EntityPlayer player) {
        if (player.getRidingEntity() instanceof WitchBroomEntity broom) {
            return new int[]{broom.getRisuniumAmount(), WitchBroomItem.MAX_RISUNIUM};
        }
        else {
            ItemStack stack = player.getHeldItemMainhand();
            if (!(stack.getItem() instanceof IRisuniumConsumer)) stack = player.getHeldItemOffhand();
            if (!(stack.getItem() instanceof IRisuniumConsumer consumer)) return new int[]{-1, -1};

            return new int[]{consumer.getRisuniumAmount(stack), consumer.getMaxRisunium()};
        }
    }
}
