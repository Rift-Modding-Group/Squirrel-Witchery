package anightdazingzoroark.squirrelwitchery.client.renderer.armor;

import anightdazingzoroark.riftlib.armor.AnimatedArmorHolder;
import anightdazingzoroark.riftlib.core.manager.AnimationDataArmor;
import anightdazingzoroark.riftlib.geo.GeoBone;
import anightdazingzoroark.riftlib.geo.GeoModel;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoArmorRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class SquirrelFurDressRenderer extends GeoArmorRenderer<AnimatedArmorHolder> {
    public SquirrelFurDressRenderer() {
        super(
                new AnimatedGeoModel<AnimatedArmorHolder>() {
                    @Override
                    @NotNull
                    public String getModId() {
                        return SquirrelWitchery.MODID;
                    }

                    @Override
                    @NotNull
                    public String getModelIdentifier(AnimatedArmorHolder animatedArmorHolder) {
                        return "geometry.squirrel_fur_dress";
                    }

                    @Override
                    @NotNull
                    public String getTextureLocation(AnimatedArmorHolder animatedArmorHolder) {
                        return "armor/squirrel_fur_dress.png";
                    }
                },
                itemStack -> new AnimatedArmorHolder(itemStack) {
                    @Override
                    public void initializeAnimationData(@NonNull AnimationDataArmor animData) {}
                }
        );

        this.bindBoneToBodyPart(ArmorBodyPart.HEAD, "hat");
        this.bindBoneToEquipmentSlot(EntityEquipmentSlot.HEAD, "hat");
        this.bindBoneToBodyPart(ArmorBodyPart.BODY, "jacket");
        this.bindBoneToEquipmentSlot(EntityEquipmentSlot.CHEST, "jacket");
        this.bindBoneToBodyPart(ArmorBodyPart.RIGHT_ARM, "rightArm");
        this.bindBoneToEquipmentSlot(EntityEquipmentSlot.CHEST, "rightArm");
        this.bindBoneToBodyPart(ArmorBodyPart.LEFT_ARM, "leftArm");
        this.bindBoneToEquipmentSlot(EntityEquipmentSlot.CHEST, "leftArm");
        this.bindBoneToBodyPart(ArmorBodyPart.HIPS, "skirt");
        this.bindBoneToEquipmentSlot(EntityEquipmentSlot.LEGS, "skirt");
        this.bindBoneToBodyPart(ArmorBodyPart.BODY, "innerDress");
        this.bindBoneToEquipmentSlot(EntityEquipmentSlot.LEGS, "innerDress");
        this.bindBoneToBodyPart(ArmorBodyPart.RIGHT_FOOT, "rightFoot");
        this.bindBoneToEquipmentSlot(EntityEquipmentSlot.FEET, "rightFoot");
        this.bindBoneToBodyPart(ArmorBodyPart.LEFT_FOOT, "leftFoot");
        this.bindBoneToEquipmentSlot(EntityEquipmentSlot.FEET, "leftFoot");
    }

    @Override
    public void render(GeoModel model, AnimatedArmorHolder animatable, float partialTicks, float red, float green, float blue, float alpha) {
        if (animatable.getAnimationData().getWearer() != null) {
            EntityLivingBase wearer = animatable.getAnimationData().getWearer();

            //---sneak adjustments---
            //skirt
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
