package anightdazingzoroark.squirrelwitchery.client.renderer.entity.layer;

import anightdazingzoroark.riftlib.geo.GeoBone;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoArmorPoseHelper;
import anightdazingzoroark.riftlib.renderers.geo.GeoArmorRenderer;
import anightdazingzoroark.riftlib.renderers.geo.IGeoArmorPoseProvider;
import anightdazingzoroark.squirrelwitchery.client.renderer.entity.MagicalSquirrelEntityRenderer;
import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.client.renderers.models.gear.ModelRobe;

public class MagicalSquirrelArmorLayer extends LayerBipedArmor {
    @NotNull
    private final MagicalSquirrelEntityRenderer renderer;
    private ModelRobe robeArmor;
    private ModelRobe robeLeggings;

    public MagicalSquirrelArmorLayer(@NotNull MagicalSquirrelEntityRenderer renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    protected void initArmor() {
        this.modelLeggings = new ArmorModel(0.5f);
        this.modelArmor = new ArmorModel(1f);
        this.robeLeggings = new RobeModel(0.5f);
        this.robeArmor = new RobeModel(1f);
    }

    @Override
    @NotNull
    protected ModelBiped getArmorModelHook(
            @NotNull EntityLivingBase entity, @NotNull ItemStack stack,
            @NotNull EntityEquipmentSlot slot, @NotNull ModelBiped model
    ) {
        ModelBiped selected = super.getArmorModelHook(entity, stack, slot, model);
        if (!(selected instanceof ModelRobe)) return selected;

        //special case for thaumcraft robes
        ModelRobe robe = switch (slot) {
            case HEAD, LEGS -> this.robeLeggings;
            default -> this.robeArmor;
        };
        robe.setModelAttributes(selected);
        return robe;
    }

    @Override
    public void doRenderLayer(
            @NotNull EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks,
            float ageInTicks, float netHeadYaw, float headPitch, float scale
    ) {
        if (!(entity instanceof MagicalSquirrelEntity squirrel) || !squirrel.isTamed() || !squirrel.getTransformed()) return;

        ModelBase model = this.renderer.getMainModel();
        model.isChild = false;
        model.isRiding = squirrel.isRiding() && squirrel.getRidingEntity() != null && squirrel.getRidingEntity().shouldRiderSit();
        model.swingProgress = squirrel.getSwingProgress(partialTicks);
        this.modelArmor.isSneak = squirrel.isSneaking();
        this.modelLeggings.isSneak = squirrel.isSneaking();

        //Vanilla armor uses a downward Y axis and an origin above the feet
        //LayerBipedArmor's Forge hooks also select registered RiftLibrary armor
        GlStateManager.pushMatrix();
        GlStateManager.scale(-1f, -1f, 1f);
        GlStateManager.translate(0f, -1.501f, 0f);
        super.doRenderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.popMatrix();
    }

    private class RobeModel extends ModelRobe {
        public RobeModel(float inflation) {
            super(inflation);
        }

        @Override
        public void setRotationAngles(
                float limbSwing, float limbSwingAmount, float ageInTicks,
                float netHeadYaw, float headPitch, float scale, @NotNull Entity entity
        ) {
            if (entity instanceof EntityLivingBase wearer) {
                GeoArmorPoseHelper.copyToBiped((ArmorModel) MagicalSquirrelArmorLayer.this.modelArmor, wearer, this);
            }
        }

        @Override
        public void render(
                @NotNull Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                float netHeadYaw, float headPitch, float scale
        ) {
            this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
            float forwardLegPitch = Math.min(this.bipedLeftLeg.rotateAngleX, this.bipedRightLeg.rotateAngleX);
            //ModelRobe drives its cloth with min(cos(swing), cos(swing + PI)) * 1.4 * amount.
            //At phase zero this reproduces the squirrel's forward leg pitch; our override
            //above keeps the limbs on their actual animated pose when ModelRobe renders.
            float clothSwingAmount = Math.max(0f, -forwardLegPitch) / 1.4f;
            super.render(entity, 0f, clothSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    private class ArmorModel extends ModelBiped implements IGeoArmorPoseProvider {
        public ArmorModel(float inflation) {
            super(inflation);
        }

        @Override
        public void setRotationAngles(
                float limbSwing, float limbSwingAmount, float ageInTicks,
                float netHeadYaw, float headPitch, float scale, @NotNull Entity entity
        ) {
            if (entity instanceof EntityLivingBase wearer) GeoArmorPoseHelper.copyToBiped(this, wearer, this);
        }

        @Override
        @Nullable
        public GeoBone getArmorPoseBone(@NotNull EntityLivingBase wearer, @NotNull GeoArmorRenderer.ArmorBodyPart part) {
            if (!(wearer instanceof MagicalSquirrelEntity squirrel)) return null;
            String name = switch (part) {
                case HEAD -> "head";
                case BODY, HIPS -> "body";
                case RIGHT_ARM -> "rightArm";
                case LEFT_ARM -> "leftArm";
                case RIGHT_LEG, RIGHT_FOOT -> "rightLeg";
                case LEFT_LEG, LEFT_FOOT -> "leftLeg";
            };
            AnimatedGeoModel<MagicalSquirrelEntity> model = (AnimatedGeoModel<MagicalSquirrelEntity>) MagicalSquirrelArmorLayer.this.renderer.getGeoModelProvider();
            return model.getModel(squirrel).getAllBones().get(name);
        }
    }
}