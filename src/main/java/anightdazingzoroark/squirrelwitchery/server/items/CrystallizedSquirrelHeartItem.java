package anightdazingzoroark.squirrelwitchery.server.items;

import anightdazingzoroark.riftlib.particle.RiftLibParticleHelper;
import anightdazingzoroark.squirrelwitchery.server.SquirrelWitcheryResearch;
import anightdazingzoroark.squirrelwitchery.server.player.SquirrelPlayerProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.SoundsTC;

public class CrystallizedSquirrelHeartItem extends Item {
    public CrystallizedSquirrelHeartItem() {
        this.setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (!world.isRemote) {
            SquirrelPlayerProperties properties = SquirrelPlayerProperties.get(player);
            if (properties == null) return new ActionResult<>(EnumActionResult.PASS, itemStack);

            properties.set("UsedHeart", !properties.hasSquirrelAttachments());
            RiftLibParticleHelper.createParticle(
                    "squirrelwitchery:risunium_heart_burst",
                    player.posX, player.posY + player.height * 0.5, player.posZ
            );
            world.playSound(
                    null, player.posX, player.posY, player.posZ,
                    SoundsTC.wand, SoundCategory.NEUTRAL, 1f, 1.15f
            );

            if (player instanceof EntityPlayerMP serverPlayer) {
                IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(serverPlayer);
                if (knowledge != null && knowledge.addResearch(SquirrelWitcheryResearch.SQUIRREL_HEART_USED)) {
                    knowledge.sync(serverPlayer);
                }
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }
}
