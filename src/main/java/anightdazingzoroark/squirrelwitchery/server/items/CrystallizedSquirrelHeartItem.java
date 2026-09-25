package anightdazingzoroark.squirrelwitchery.server.items;

import anightdazingzoroark.squirrelwitchery.server.player.SquirrelPlayerProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class CrystallizedSquirrelHeartItem extends Item {
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        SquirrelPlayerProperties properties = SquirrelPlayerProperties.get(player);
        if (properties == null || properties.hasSquirrelAttachments()) {
            return new ActionResult<>(EnumActionResult.PASS, itemStack);
        }

        if (!world.isRemote) {
            properties.set("UsedHeart", true);
            if (!player.capabilities.isCreativeMode) itemStack.shrink(1);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }
}
