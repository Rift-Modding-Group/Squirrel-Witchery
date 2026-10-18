package anightdazingzoroark.squirrelwitchery.server.effect;

import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.player.SquirrelPlayerProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

public class GWSEffect extends Potion {
    private static final ResourceLocation ICON_TEXTURE = new ResourceLocation(SquirrelWitchery.MODID, "textures/items/gws_pill.png");

    public GWSEffect() {
        super(false, 0xB37D4F);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 60 == 1;
    }

    //the gws effect will reduce permanent warp by 1 every 3 seconds
    //if gone, all le permanent warp will come back
    @Override
    public void performEffect(EntityLivingBase entityLivingBase, int amplifier) {
        if (!(entityLivingBase instanceof EntityPlayerMP player)) return;

        IPlayerWarp warp = ThaumcraftCapabilities.getWarp(player);
        SquirrelPlayerProperties properties = SquirrelPlayerProperties.get(player);
        if (warp == null || properties == null || warp.get(IPlayerWarp.EnumWarpType.PERMANENT) <= 0) return;

        warp.reduce(IPlayerWarp.EnumWarpType.PERMANENT, 1);
        properties.setGWSSuppressedWarp(properties.getGWSSuppressedWarp() + 1);
        warp.sync(player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ICON_TEXTURE);
        GlStateManager.color(1f, 1f, 1f, 1f);
        Gui.drawScaledCustomSizeModalRect(x + 6, y + 7, 0f, 0f, 16, 16, 18, 18, 16f, 16f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ICON_TEXTURE);
        GlStateManager.color(1f, 1f, 1f, alpha);
        Gui.drawScaledCustomSizeModalRect(x + 3, y + 3, 0f, 0f, 16, 16, 18, 18, 16f, 16f);
    }
}
