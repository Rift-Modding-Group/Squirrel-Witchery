package anightdazingzoroark.squirrelwitchery.mixin;

import anightdazingzoroark.squirrelwitchery.server.SquirrelWitcheryResearch;
import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.crafting.TileCrucible;

import java.util.HashSet;
import java.util.Set;

@Mixin(value = TileCrucible.class, remap = false)
public abstract class TileCrucibleMixin {
    @Inject(
            method = "attemptSmelt(Lnet/minecraft/item/ItemStack;Ljava/lang/String;)Lnet/minecraft/item/ItemStack;",
            at = @At("HEAD"), cancellable = true, require = 1
    )
    private void reviveMagicalSquirrel(@NotNull ItemStack item, @Nullable String username, @NotNull CallbackInfoReturnable<ItemStack> callback) {
        if (item.getItem() != SquirrelWitcheryItems.MAGICAL_SQUIRREL_ESSENCE) return;

        //essence must never be dissolved in the crucible
        callback.setReturnValue(item);

        //make sure essence has nbt to be able to revive
        NBTTagCompound tag = item.getTagCompound();
        if (tag == null || !tag.hasKey("MagicalSquirrel", 10)) return;

        TileCrucible crucible = (TileCrucible) (Object) this;
        World world = crucible.getWorld();
        if (world.isRemote || crucible.heat <= 150 || crucible.tank.getFluidAmount() <= 0
                || crucible.aspects.getAmount(SquirrelWitcheryAspects.RISUNIUM) < 75
        ) {
            return;
        }

        BlockPos pos = crucible.getPos();
        //---recreate the magical squirrel---
        MagicalSquirrelEntity squirrel = new MagicalSquirrelEntity(world);
        NBTTagCompound squirrelData = tag.getCompoundTag("MagicalSquirrel");
        if (squirrelData.hasUniqueId("Owner")) {
            squirrel.setOwnerId(squirrelData.getUniqueId("Owner"));
            squirrel.setTamed(true);
        }
        squirrel.setTransformed(squirrelData.getBoolean("Transformed"));
        MagicalSquirrelEntity.Behavior[] behaviors = MagicalSquirrelEntity.Behavior.values();
        squirrel.setBehavior(behaviors[Math.clamp(squirrelData.getByte("Behavior"), 0, behaviors.length - 1)]);
        squirrel.setCustomNameTag(squirrelData.getString("CustomName"));
        squirrel.setAlwaysRenderNameTag(squirrelData.getBoolean("CustomNameVisible"));
        squirrel.setHealth(squirrel.getMaxHealth());

        //---find a valid space to spawn the squirrel in---
        boolean foundSpace = false;
        outer: for (int yOffset = -1; yOffset <= 1; yOffset++) {
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    if (xOffset == 0 && zOffset == 0) continue;
                    BlockPos spawnPos = pos.add(xOffset, yOffset, zOffset);
                    if (!world.getBlockState(spawnPos.down()).isTopSolid()) continue;
                    squirrel.setLocationAndAngles(
                            spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                            world.rand.nextFloat() * 360f, 0f
                    );
                    if (!world.getCollisionBoxes(squirrel, squirrel.getEntityBoundingBox()).isEmpty()
                            || world.containsAnyLiquid(squirrel.getEntityBoundingBox())
                    ) {
                        continue;
                    }
                    foundSpace = true;
                    break outer;
                }
            }
        }
        if (!foundSpace) return;

        //---perform le revival---
        if (!world.spawnEntity(squirrel)) return;

        //revival consumes the entire crucible contents
        crucible.aspects = new AspectList();
        crucible.tank.setFluid(null);
        item.shrink(1);
        crucible.markDirty();
        crucible.syncTile(false);
        world.playSound(
                null, pos.getX(), pos.getY(), pos.getZ(),
                SoundsTC.wand, SoundCategory.NEUTRAL, 1f, 1.15f
        );
        world.addBlockEvent(pos, BlocksTC.crucible, 99, 0);

        //---update research for player---
        Set<EntityPlayerMP> researchers = new HashSet<>();
        //pass owner as potential researcher
        if (squirrel.getOwnerId() != null && world.getMinecraftServer() != null) {
            EntityPlayerMP owner = world.getMinecraftServer().getPlayerList().getPlayerByUUID(squirrel.getOwnerId());
            if (owner != null) researchers.add(owner);
        }
        //pass whoever threw the essence as potential researcher
        if (username != null && world.getPlayerEntityByName(username) instanceof EntityPlayerMP player) researchers.add(player);

        //update research on researchers
        for (EntityPlayerMP researcher : researchers) {
            IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(researcher);
            if (knowledge != null && knowledge.addResearch(SquirrelWitcheryResearch.MAGICAL_SQUIRREL_REVIVED)) {
                knowledge.sync(researcher);
            }
        }
        if (item.isEmpty()) callback.setReturnValue(null);
    }
}
