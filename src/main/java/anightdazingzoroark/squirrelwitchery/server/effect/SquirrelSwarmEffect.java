package anightdazingzoroark.squirrelwitchery.server.effect;

import anightdazingzoroark.riftlib.particle.RiftLibParticleHelper;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.entity.SwarmerSquirrelEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import thaumcraft.common.lib.SoundsTC;

public class SquirrelSwarmEffect extends Potion {
    private static final ResourceLocation ICON_TEXTURE = new ResourceLocation(SquirrelWitchery.MODID, "textures/misc/squirrel_swarm.png");

    public SquirrelSwarmEffect() {
        super(true, 0xAE3E3E);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 10 == 0;
    }

    @Override
    public void performEffect(@NotNull EntityLivingBase target, int amplifier) {
        World world = target.world;
        if (world.isRemote || !target.isEntityAlive() || target.getIsInvulnerable()) return;

        //max out sqrl amount to 12 per target
        int activeSwarmers = world.getEntities(
                SwarmerSquirrelEntity.class,
                squirrel -> squirrel.isEntityAlive() && squirrel.getAttackTarget() == target
        ).size();
        if (activeSwarmers >= 12) return;

        //attempt to spawn squirrels near the target
        int spawnCount = Math.min(world.rand.nextInt(2, 5), 12 - activeSwarmers);
        outer: for (int index = 0; index < spawnCount; index++) {
            SwarmerSquirrelEntity squirrel = new SwarmerSquirrelEntity(world, target, amplifier + 1D);
            for (int attempt = 0; attempt < 12; attempt++) {
                double angle = world.rand.nextDouble() * Math.TAU;
                double radius = target.width * 0.5D + 1D + world.rand.nextDouble() * 1.5D;
                BlockPos column = new BlockPos(
                        target.posX + Math.cos(angle) * radius,
                        target.getEntityBoundingBox().minY,
                        target.posZ + Math.sin(angle) * radius
                );
                for (int offset = 2; offset >= -2; offset--) {
                    BlockPos feet = column.up(offset);
                    BlockPos ground = feet.down();
                    if (!world.isBlockLoaded(ground) || !world.getBlockState(ground).isSideSolid(world, ground, EnumFacing.UP)) continue;

                    squirrel.setLocationAndAngles(feet.getX() + 0.5D, feet.getY(), feet.getZ() + 0.5D, world.rand.nextFloat() * 360f, 0f);
                    if (!world.getCollisionBoxes(squirrel, squirrel.getEntityBoundingBox()).isEmpty()
                            || !world.checkNoEntityCollision(squirrel.getEntityBoundingBox())
                    ) {
                        continue;
                    }

                    //successful attempt, burst into orbs, play sound and move on to the next squirrel
                    if (world.spawnEntity(squirrel)) {
                        RiftLibParticleHelper.createParticle(
                                "squirrelwitchery:risunium_heart_burst",
                                squirrel.posX, squirrel.posY + squirrel.height * 0.5D, squirrel.posZ
                        );
                        world.playSound(
                                null, squirrel.posX, squirrel.posY, squirrel.posZ,
                                SoundsTC.wand, SoundCategory.NEUTRAL, 1f, 1.15f
                        );
                        continue outer;
                    }
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(@NotNull PotionEffect effect, @NotNull Gui gui, int x, int y, float z) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ICON_TEXTURE);
        GlStateManager.color(1f, 1f, 1f, 1f);
        Gui.drawScaledCustomSizeModalRect(x + 6, y + 7, 0f, 0f, 16, 16, 18, 18, 16f, 16f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUDEffect(@NotNull PotionEffect effect, @NotNull Gui gui, int x, int y, float z, float alpha) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ICON_TEXTURE);
        GlStateManager.color(1f, 1f, 1f, alpha);
        Gui.drawScaledCustomSizeModalRect(x + 3, y + 3, 0f, 0f, 16, 16, 18, 18, 16f, 16f);
    }
}