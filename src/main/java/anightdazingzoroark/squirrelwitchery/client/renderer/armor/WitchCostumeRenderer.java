package anightdazingzoroark.squirrelwitchery.client.renderer.armor;

import anightdazingzoroark.riftlib.armor.AnimatedArmorHolder;
import anightdazingzoroark.riftlib.core.manager.AnimationDataArmor;
import anightdazingzoroark.riftlib.geo.GeoBone;
import anightdazingzoroark.riftlib.geo.GeoModel;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoArmorRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.jetbrains.annotations.NotNull;
import thaumcraft.common.items.casters.ItemCaster;

public class WitchCostumeRenderer extends GeoArmorRenderer<AnimatedArmorHolder> {
    public WitchCostumeRenderer() {
        super(new AnimatedGeoModel<AnimatedArmorHolder>() {
                @Override
                @NotNull
                public String getModId() {
                    return SquirrelWitchery.MODID;
                }

                @Override
                @NotNull
                public String getModelIdentifier(AnimatedArmorHolder witchCostumeHolder) {
                    return "geometry.witch_costume";
                }

                @Override
                @NotNull
                public String getTextureLocation(AnimatedArmorHolder witchCostumeHolder) {
                    Item item = witchCostumeHolder.getAnimationData().getStack().getItem();
                    if (item == SquirrelWitcheryItems.DARK_WITCH_HAT || item == SquirrelWitcheryItems.DARK_WITCH_ROBE
                            || item == SquirrelWitcheryItems.DARK_WITCH_SKIRT || item == SquirrelWitcheryItems.DARK_WITCH_BOOTS
                    ) {
                        return "armor/dark_witch_costume.png";
                    }
                    return "armor/witch_costume.png";
                }
            },
            itemStack -> new AnimatedArmorHolder(itemStack) {
                @Override
                public void initializeAnimationData(@NotNull AnimationDataArmor animationDataArmor) {}
            }
        );

        this.bindBoneToBodyPart(ArmorBodyPart.HEAD, "hat");
        this.bindBoneToEquipmentSlot(EntityEquipmentSlot.HEAD, "hat");
        this.bindBoneToBodyPart(ArmorBodyPart.BODY, "robe");
        this.bindBoneToEquipmentSlot(EntityEquipmentSlot.CHEST, "robe");
        this.bindBoneToBodyPart(ArmorBodyPart.RIGHT_ARM, "rightArm");
        this.bindBoneToEquipmentSlot(EntityEquipmentSlot.CHEST, "rightArm");
        this.bindBoneToBodyPart(ArmorBodyPart.LEFT_ARM, "leftArm");
        this.bindBoneToEquipmentSlot(EntityEquipmentSlot.CHEST, "leftArm");
        this.bindBoneToBodyPart(ArmorBodyPart.HIPS, "skirt");
        this.bindBoneToEquipmentSlot(EntityEquipmentSlot.LEGS, "skirt");
        this.bindBoneToBodyPart(ArmorBodyPart.RIGHT_FOOT, "rightFoot");
        this.bindBoneToEquipmentSlot(EntityEquipmentSlot.FEET, "rightFoot");
        this.bindBoneToBodyPart(ArmorBodyPart.LEFT_FOOT, "leftFoot");
        this.bindBoneToEquipmentSlot(EntityEquipmentSlot.FEET, "leftFoot");
    }

    @Override
    public void render(GeoModel model, AnimatedArmorHolder animatable, float partialTicks, float red, float green, float blue, float alpha) {
        if (animatable.getAnimationData().getWearer() != null) {
            EntityLivingBase wearer = animatable.getAnimationData().getWearer();

            //---hide glove when wearing gauntlet---
            //hide right glove
            ItemStack rightItemStack = wearer.getHeldItem(EnumHand.MAIN_HAND);
            GeoBone rightGloveBone = model.getAllBones().get("rightGlove");
            if (rightGloveBone != null) rightGloveBone.setHidden(rightItemStack != null && isGauntlet(rightItemStack));

            //same w left glove
            ItemStack leftItemStack = wearer.getHeldItem(EnumHand.OFF_HAND);
            GeoBone leftGloveBone = model.getAllBones().get("leftGlove");
            if (leftGloveBone != null) leftGloveBone.setHidden(leftItemStack != null && isGauntlet(leftItemStack));

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

    private static boolean isGauntlet(@NotNull ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemCaster && itemStack.getItem() != SquirrelWitcheryItems.WITCH_STAFF;
    }
}