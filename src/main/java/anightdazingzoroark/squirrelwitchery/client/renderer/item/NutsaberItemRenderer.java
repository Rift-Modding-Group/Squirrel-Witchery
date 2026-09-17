package anightdazingzoroark.squirrelwitchery.client.renderer.item;

import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.controller.AnimationControllerState;
import anightdazingzoroark.riftlib.core.manager.AnimationDataItemStack;
import anightdazingzoroark.riftlib.geo.GeoBone;
import anightdazingzoroark.riftlib.geo.GeoModel;
import anightdazingzoroark.riftlib.item.AnimatedItemStackHolder;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoItemRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.items.NutsaberItem;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NutsaberItemRenderer extends GeoItemRenderer<AnimatedItemStackHolder> {
    public NutsaberItemRenderer() {
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
                        return "geometry.nutsaber";
                    }

                    @Override
                    @NotNull
                    public String getTextureLocation(AnimatedItemStackHolder animatedItemStackHolder) {
                        return "items/nutsaber.png";
                    }
                },
                itemStack -> new AnimatedItemStackHolder(itemStack) {
                    @Override
                    public void initializeAnimationData(@NotNull AnimationDataItemStack animationDataItemStack) {
                        animationDataItemStack.addAnimationController(new AnimationController<AnimatedItemStackHolder, AnimationDataItemStack>(this, "display", "default",
                                new AnimationControllerState<AnimationDataItemStack>("default", 0.2)
                                        .addAnimation("animation.nutsaber.default")
                                        .addStateTransition("unsheathed", animData -> {
                                            //make sure it animates when it starts ticking
                                            if (animData.tick <= 0) return false;
                                            if (this.getStack().getItem() != SquirrelWitcheryItems.NUTSABER) return false;
                                            if (!this.isHeld()) return false;
                                            ItemCameraTransforms.TransformType transformType = this.getTransformType();
                                            if ((transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                                                    || transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)
                                                    && !this.isFirstPersonEquipAnimationComplete()) return false;
                                            return SquirrelWitcheryItems.NUTSABER.getRisuniumAmount(this.getStack()) > 0;
                                        }),
                                new AnimationControllerState<AnimationDataItemStack>("unsheathed", 0.2)
                                        .addAnimation("animation.nutsaber.reveal")
                                        .addStateTransition("default", animData -> {
                                            return this.getStack().getItem() != SquirrelWitcheryItems.NUTSABER
                                                    || SquirrelWitcheryItems.NUTSABER.getRisuniumAmount(this.getStack()) <= 0
                                                    || !this.isHeld();
                                        })
                        ));
                    }
                }
        );
    }

    @Override
    public void render(GeoModel model, AnimatedItemStackHolder animatable, float partialTicks, float red, float green, float blue, float alpha) {
        ItemStack stack = animatable.getStack();
        GeoBone bladeBone = model.getAllBones().get("blade");
        if (bladeBone != null && stack.getItem() instanceof NutsaberItem) {
            bladeBone.setHidden(animatable.getTransformType() == ItemCameraTransforms.TransformType.GUI);
        }

        super.render(model, animatable, partialTicks, red, green, blue, alpha);
    }

    @Override
    public boolean makeTranslucencyGlow() {
        return true;
    }
}
