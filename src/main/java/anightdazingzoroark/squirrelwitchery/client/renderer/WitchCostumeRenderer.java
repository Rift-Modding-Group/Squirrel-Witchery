package anightdazingzoroark.squirrelwitchery.client.renderer;

import anightdazingzoroark.riftlib.geo.GeoBone;
import anightdazingzoroark.riftlib.geo.GeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoArmorRenderer;
import anightdazingzoroark.squirrelwitchery.client.model.WitchCostumeModel;
import anightdazingzoroark.squirrelwitchery.server.armor.WitchCostumeHolder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import thaumcraft.common.items.casters.ItemCaster;

public class WitchCostumeRenderer extends GeoArmorRenderer<WitchCostumeHolder> {
    public WitchCostumeRenderer() {
        super(new WitchCostumeModel(), WitchCostumeHolder::new);
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
