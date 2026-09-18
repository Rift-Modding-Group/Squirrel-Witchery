package anightdazingzoroark.squirrelwitchery.server.items;

import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.entity.WitchBroomEntity;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.essentia.BlockJarItem;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class WitchPendantItem extends Item implements IBauble {
    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.AMULET;
    }

    /**
    * this is to automatically draw from risunium jars in the players inventory every second
     * <br />
    * waitin for that fast world query proposal for cleanroom to be added to
    * also include jars placed in the world
    * */
    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase entity) {
        if (entity.world.isRemote || !(entity instanceof EntityPlayer player)) return;

        //make sure refill happens every second
        if (player.ticksExisted % 20 != 0) return;

        //resupply on both hands
        resupplyRisuniumConsumer(
                player,
                () -> {
                    ItemStack mainItemStack = player.getHeldItemMainhand();
                    if (!(mainItemStack.getItem() instanceof IRisuniumConsumer risuniumConsumerItem)) return false;
                    return risuniumConsumerItem.getRisuniumAmount(mainItemStack) < risuniumConsumerItem.getMaxRisunium();
                },
                amount -> {
                    ItemStack mainItemStack = player.getHeldItemMainhand();
                    if (!(mainItemStack.getItem() instanceof IRisuniumConsumer risuniumConsumerItem)) return;
                    int oldAmount = risuniumConsumerItem.getRisuniumAmount(mainItemStack);
                    risuniumConsumerItem.setRisuniumAmount(mainItemStack, oldAmount + amount);
                }
        );
        resupplyRisuniumConsumer(
                player,
                () -> {
                    ItemStack offItemStack = player.getHeldItemOffhand();
                    if (!(offItemStack.getItem() instanceof IRisuniumConsumer risuniumConsumerItem)) return false;
                    return risuniumConsumerItem.getRisuniumAmount(offItemStack) < risuniumConsumerItem.getMaxRisunium();
                },
                amount -> {
                    ItemStack offItemStack = player.getHeldItemOffhand();
                    if (!(offItemStack.getItem() instanceof IRisuniumConsumer risuniumConsumerItem)) return;
                    int oldAmount = risuniumConsumerItem.getRisuniumAmount(offItemStack);
                    risuniumConsumerItem.setRisuniumAmount(offItemStack, oldAmount + amount);
                }
        );

        //resupply ridden broom
        resupplyRisuniumConsumer(
                player,
                () -> {
                    return player.getRidingEntity() instanceof WitchBroomEntity witchBroomEntity
                            && witchBroomEntity.getRisuniumAmount() < WitchBroomEntity.MAX_RISUNIUM;
                },
                amount -> {
                    if (!(player.getRidingEntity() instanceof WitchBroomEntity witchBroomEntity)) return;
                    witchBroomEntity.setRisuniumAmount(witchBroomEntity.getRisuniumAmount() + amount);
                }
        );
    }

    //very general helper
    private static void resupplyRisuniumConsumer(EntityPlayer player, @NotNull Supplier<Boolean> condition, @NotNull Consumer<Integer> risuniumGiver) {
        if (!condition.get()) return;

        //iterate over inventory
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!(stack.getItem() instanceof BlockJarItem jarItem)) continue;

            AspectList aspects = jarItem.getAspects(stack);
            if (aspects == null) continue;

            int available = aspects.getAmount(SquirrelWitcheryAspects.RISUNIUM);
            if (available <= 0) continue;

            //transfer risunium from found jar to the item
            aspects.remove(SquirrelWitcheryAspects.RISUNIUM, 1);
            jarItem.setAspects(stack, aspects);
            risuniumGiver.accept(1);
            player.world.playSound(
                    null,
                    player.posX, player.posY, player.posZ,
                    SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS,
                    0.7f, 1f
            );
            player.inventoryContainer.detectAndSendChanges();
            break;
        }
    }
}