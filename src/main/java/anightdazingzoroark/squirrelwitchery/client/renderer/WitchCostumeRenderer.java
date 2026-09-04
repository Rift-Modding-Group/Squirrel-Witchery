package anightdazingzoroark.squirrelwitchery.client.renderer;

import anightdazingzoroark.riftlib.geo.GeoBone;
import anightdazingzoroark.riftlib.geo.GeoModel;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoArmorRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.armor.WitchCostumeHolder;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.jetbrains.annotations.NotNull;
import thaumcraft.common.items.casters.ItemCaster;

public class WitchCostumeRenderer extends GeoArmorRenderer<WitchCostumeHolder> {
    public WitchCostumeRenderer() {
        super(new AnimatedGeoModel<WitchCostumeHolder>() {
                @Override
                @NotNull
                public String getModId() {
                    return SquirrelWitchery.MODID;
                }

                @Override
                public String getModelIdentifier(WitchCostumeHolder witchCostumeHolder) {
                    return "geometry.witch_costume";
                }

                @Override
                public String getTextureLocation(WitchCostumeHolder witchCostumeHolder) {
                    Item item = witchCostumeHolder.getAnimationData().getStack().getItem();
                    if (item == SquirrelWitcheryItems.DARK_WITCH_HAT || item == SquirrelWitcheryItems.DARK_WITCH_ROBE
                            || item == SquirrelWitcheryItems.DARK_WITCH_SKIRT || item == SquirrelWitcheryItems.DARK_WITCH_BOOTS
                    ) {
                        return "armor/dark_witch_costume.png";
                    }
                    return "armor/witch_costume.png";
                }
            },
            WitchCostumeHolder::new
        );
        this.setHeadBone("hat");
        this.setBodyBone("robe");
        this.setRightArmBone("rightArm");
        this.setLeftArmBone("leftArm");
        this.setHipsBone("skirt");
        this.setRightBootBone("rightFoot");
        this.setLeftBootBone("leftFoot");
    }

    @Override
    public void render(GeoModel model, WitchCostumeHolder animatable, float partialTicks, float red, float green, float blue, float alpha) {
        if (animatable.getAnimationData().getWearer() != null) {
            EntityLivingBase wearer = animatable.getAnimationData().getWearer();

            //---hide glove when wearing gauntlet---
            //hide right glove
            ItemStack rightItemStack = wearer.getHeldItem(EnumHand.MAIN_HAND);
            GeoBone rightGloveBone = model.getAllBones().get("rightGlove");
            if (rightGloveBone != null) rightGloveBone.setHidden(rightItemStack != null && rightItemStack.getItem() instanceof ItemCaster);

            //same w left glove
            ItemStack leftItemStack = wearer.getHeldItem(EnumHand.OFF_HAND);
            GeoBone leftGloveBone = model.getAllBones().get("leftGlove");
            if (leftGloveBone != null) leftGloveBone.setHidden(leftItemStack != null && leftItemStack.getItem() instanceof ItemCaster);

            //---sneak adjustments---
            GeoBone skirt = model.getAllBones().get("skirt");
            GeoBone skirtFront = model.getAllBones().get("skirtFront");
            if (wearer.isSneaking() && skirt != null && skirtFront != null) {
                skirtFront.getRotation().x -= skirt.getRotation().x;
                skirtFront.getRotation().y -= skirt.getRotation().y;
                skirtFront.getRotation().z -= skirt.getRotation().z;
                skirtFront.getPosition().z += 4f;
            }
        }

        super.render(model, animatable, partialTicks, red, green, blue, alpha);
    }
}
