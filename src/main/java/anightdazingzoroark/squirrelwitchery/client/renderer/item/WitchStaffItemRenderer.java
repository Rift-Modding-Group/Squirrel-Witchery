package anightdazingzoroark.squirrelwitchery.client.renderer.item;

import anightdazingzoroark.riftlib.core.manager.AnimationDataItemStack;
import anightdazingzoroark.riftlib.geo.GeoBone;
import anightdazingzoroark.riftlib.geo.GeoModel;
import anightdazingzoroark.riftlib.item.AnimatedItemStackHolder;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoItemRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.items.WitchStaffAttachmentItem;
import anightdazingzoroark.squirrelwitchery.server.items.WitchStaffItem;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class WitchStaffItemRenderer extends GeoItemRenderer<AnimatedItemStackHolder> {
    public WitchStaffItemRenderer() {
        super(
                new AnimatedGeoModel<AnimatedItemStackHolder>() {
                    @Override
                    @NotNull
                    public String getModId() {
                        return SquirrelWitchery.MODID;
                    }

                    @Override
                    @NotNull
                    public String getModelIdentifier(AnimatedItemStackHolder animatedItemStackHolder) {
                        return "geometry.witch_staff";
                    }

                    @Override
                    @NotNull
                    public String getTextureLocation(AnimatedItemStackHolder animatedItemStackHolder) {
                        ItemStack stack = animatedItemStackHolder.getStack();
                        if (stack.getItem() instanceof WitchStaffItem staff) {
                            WitchStaffAttachmentItem.Type attachment = staff.getAttachment(stack);
                            if (attachment != null) {
                                return "items/witch_staff_focal_" + attachment.name().toLowerCase(Locale.ROOT) + ".png";
                            }
                        }
                        return "items/witch_staff.png";
                    }
                },
                itemStack -> new AnimatedItemStackHolder(itemStack) {
                    @Override
                    public void initializeAnimationData(@NotNull AnimationDataItemStack animationDataItemStack) {}
                }
        );
    }

    @Override
    public void render(GeoModel model, AnimatedItemStackHolder animatable, float partialTicks, float red, float green, float blue, float alpha) {
        ItemStack stack = animatable.getStack();
        GeoBone attachment = model.getAllBones().get("attachment");
        if (attachment != null && stack.getItem() instanceof WitchStaffItem staff) {
            attachment.setHidden(staff.getAttachment(stack) == null);
        }

        super.render(model, animatable, partialTicks, red, green, blue, alpha);
    }
}
