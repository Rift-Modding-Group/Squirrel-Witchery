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
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.client.renderers.models.gear.ModelFortressArmor;
import thaumcraft.client.renderers.models.gear.ModelRobe;

import java.util.IdentityHashMap;
import java.util.Map;

public class MagicalSquirrelArmorLayer extends LayerBipedArmor implements IGeoArmorPoseProvider {
    @NotNull
    private final MagicalSquirrelEntityRenderer renderer;
    @NotNull
    private final Map<ModelBiped, ModelBiped> armorModels = new IdentityHashMap<>();

    public MagicalSquirrelArmorLayer(@NotNull MagicalSquirrelEntityRenderer renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    protected void initArmor() {
        this.modelLeggings = new MagicalSquirrelBipedModel(0.5f);
        this.modelArmor = new MagicalSquirrelBipedModel(1f);
    }

    @Override
    @NotNull
    protected ModelBiped getArmorModelHook(
            @NotNull EntityLivingBase entity, @NotNull ItemStack stack,
            @NotNull EntityEquipmentSlot slot, @NotNull ModelBiped model
    ) {
        ModelBiped selected = super.getArmorModelHook(entity, stack, slot, model);
        ModelBiped posed = this.armorModels.computeIfAbsent(selected, original -> {
            float inflation = switch (slot) {
                case HEAD, LEGS -> 0.5f;
                default -> 1f;
            };
            if (original instanceof ModelRobe) return new RobeModel(inflation);
            if (original instanceof ModelFortressArmor) return new FortressModel(inflation);
            //Identify the optional mod's model without requiring Antique Armory to be installed.
            //Its broken/concealed armor model must retain its empty render implementation.
            if (original.getClass().getName().equals("c4.conarm.client.models.ModelConstructsArmorFixed")) {
                return new AntiqueArmoryModel(original);
            }
            return original;
        });

        posed.setModelAttributes(selected);
        return posed;
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

        GlStateManager.pushMatrix();
        GlStateManager.scale(-1f, -1f, 1f);
        GlStateManager.translate(0f, -1.501f, 0f);
        super.doRenderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.popMatrix();
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
        AnimatedGeoModel<MagicalSquirrelEntity> model = (AnimatedGeoModel<MagicalSquirrelEntity>) this.renderer.getGeoModelProvider();
        return model.getModel(squirrel).getAllBones().get(name);
    }

    private void copyArmorPose(@NotNull Entity entity, @NotNull ModelBiped model) {
        if (entity instanceof EntityLivingBase wearer) GeoArmorPoseHelper.copyToBiped(this, wearer, model);
    }

    //-----subclasses for armor-----
    //help out w riftlib armors
    private class MagicalSquirrelBipedModel extends ModelBiped implements IGeoArmorPoseProvider {
        public MagicalSquirrelBipedModel(float inflation) {
            super(inflation);
        }

        @Override
        public void setRotationAngles(
                float limbSwing, float limbSwingAmount, float ageInTicks,
                float netHeadYaw, float headPitch, float scale, @NotNull Entity entity
        ) {
            MagicalSquirrelArmorLayer.this.copyArmorPose(entity, this);
        }

        //RiftLibrary obtains the pose provider from the default model passed to Forge's hook.
        @Override
        @Nullable
        public GeoBone getArmorPoseBone(@NotNull EntityLivingBase wearer, @NotNull GeoArmorRenderer.ArmorBodyPart part) {
            return MagicalSquirrelArmorLayer.this.getArmorPoseBone(wearer, part);
        }
    }

    //thaumic robe
    private class RobeModel extends ModelRobe {
        public RobeModel(float inflation) {
            super(inflation);
        }

        @Override
        public void setRotationAngles(
                float limbSwing, float limbSwingAmount, float ageInTicks,
                float netHeadYaw, float headPitch, float scale, @NotNull Entity entity
        ) {
            MagicalSquirrelArmorLayer.this.copyArmorPose(entity, this);
        }

        @Override
        public void render(
                @NotNull Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                float netHeadYaw, float headPitch, float scale
        ) {
            this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
            float forwardLegPitch = Math.min(this.bipedLeftLeg.rotateAngleX, this.bipedRightLeg.rotateAngleX);
            float clothSwingAmount = Math.max(0f, -forwardLegPitch) / 1.4f;
            super.render(entity, 0f, clothSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    //thaum fortress
    private class FortressModel extends ModelFortressArmor {
        public FortressModel(float inflation) {
            super(inflation);
        }

        @Override
        public void setRotationAngles(
                float limbSwing, float limbSwingAmount, float ageInTicks,
                float netHeadYaw, float headPitch, float scale, @NotNull Entity entity
        ) {
            MagicalSquirrelArmorLayer.this.copyArmorPose(entity, this);
        }
    }

    //antique armory
    private class AntiqueArmoryModel extends MagicalSquirrelBipedModel {
        public AntiqueArmoryModel(@NotNull ModelBiped selected) {
            super(0f);
            this.bipedHeadwear.cubeList.clear();
            for (GeoArmorRenderer.ArmorBodyPart part : GeoArmorRenderer.ArmorBodyPart.values()) {
                if (part == GeoArmorRenderer.ArmorBodyPart.HIPS || part == GeoArmorRenderer.ArmorBodyPart.RIGHT_FOOT
                        || part == GeoArmorRenderer.ArmorBodyPart.LEFT_FOOT
                ) {
                    continue;
                }

                ModelRenderer source = part.getBipedBone(selected);
                ModelRenderer target = part.getBipedBone(this);
                target.cubeList.clear();
                ModelRenderer geometry = new ModelRenderer(this);
                geometry.cubeList.addAll(source.cubeList);
                geometry.childModels = source.childModels;
                geometry.showModel = source.showModel;
                geometry.isHidden = source.isHidden;
                geometry.rotationPointX = switch (part) {
                    case RIGHT_LEG -> 1.9f;
                    case LEFT_LEG -> -1.9f;
                    default -> 0f;
                };
                target.addChild(geometry);
            }
        }
    }
}