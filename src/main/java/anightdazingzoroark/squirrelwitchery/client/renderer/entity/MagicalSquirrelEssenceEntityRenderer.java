package anightdazingzoroark.squirrelwitchery.client.renderer.entity;

import anightdazingzoroark.riftlib.model.AnimatedGeoModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoEntityNonLivingRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEssenceEntity;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MagicalSquirrelEssenceEntityRenderer extends GeoEntityNonLivingRenderer<MagicalSquirrelEssenceEntity> {
    public MagicalSquirrelEssenceEntityRenderer(@NotNull RenderManager renderManager) {
        super(renderManager, new AnimatedGeoModel<MagicalSquirrelEssenceEntity>() {
            @Override
            @NotNull
            public String getModId() {
                return SquirrelWitchery.MODID;
            }

            @Override
            @NotNull
            public String getModelIdentifier(MagicalSquirrelEssenceEntity magicalSquirrelEssenceEntity) {
                return "geometry.magical_squirrel_essence";
            }

            @Override
            @NotNull
            public String getTextureLocation(MagicalSquirrelEssenceEntity magicalSquirrelEssenceEntity) {
                return "entity/magical_squirrel_essence.png";
            }
        });
    }

    @Override
    @NotNull
    public ItemStack getItemForLocator(@NotNull MagicalSquirrelEssenceEntity entity, @NotNull String locatorName, float partialTicks) {
        if (locatorName.equals("base")) return entity.getEssenceItem();
        return ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemCameraTransforms.TransformType getItemTransformTypeForLocator(
            @NotNull MagicalSquirrelEssenceEntity entity, @NotNull String locatorName, @NotNull ItemStack stack, float partialTicks
    ) {
        return ItemCameraTransforms.TransformType.GROUND;
    }
}