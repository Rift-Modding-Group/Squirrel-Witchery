package anightdazingzoroark.squirrelwitchery.client.renderer.player;

import anightdazingzoroark.riftlib.geo.GeoBone;
import anightdazingzoroark.riftlib.geo.GeoModel;
import anightdazingzoroark.riftlib.model.provider.GeoModelProvider;
import anightdazingzoroark.riftlib.renderers.geo.IGeoRenderer;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.player.SquirrelPlayerProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import org.jetbrains.annotations.NotNull;

public class SquirrelAttachmentsLayer implements LayerRenderer<AbstractClientPlayer>, IGeoRenderer<AbstractClientPlayer> {
    private final RenderPlayer playerRenderer;
    private final GeoModelProvider<AbstractClientPlayer> modelProvider;
    private GeoModel sourceModel;
    private GeoModel model;
    private float earsPositionY;
    private float earsRotationX;
    private float earsRotationY;
    private float earsRotationZ;
    private float tailPositionY;
    private float tailPositionZ;
    private float tailRotationX;
    private float tailRotationY;
    private float tailRotationZ;

    public SquirrelAttachmentsLayer(RenderPlayer playerRenderer) {
        this.playerRenderer = playerRenderer;
        this.modelProvider = new GeoModelProvider<>() {
            @Override
            @NotNull
            public String getModId() {
                return SquirrelWitchery.MODID;
            }

            @Override
            @NotNull
            public String getModelIdentifier(AbstractClientPlayer player) {
                return "geometry.squirrel_attachments";
            }

            @Override
            @NotNull
            public String getTextureLocation(AbstractClientPlayer player) {
                return "misc/squirrel_attachments.png";
            }
        };
    }

    @Override
    public void doRenderLayer(
            AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks,
            float ageInTicks, float netHeadYaw, float headPitch, float scale
    ) {
        SquirrelPlayerProperties properties = SquirrelPlayerProperties.get(player);
        if (properties == null || !properties.hasSquirrelAttachments() || player.isInvisible()) return;

        GeoModel currentSourceModel = this.modelProvider.getModel(this.modelProvider.getModelIdentifier(player));
        if (currentSourceModel != this.sourceModel) {
            this.sourceModel = currentSourceModel;
            this.model = currentSourceModel.copy();
            GeoBone ears = this.model.getAllBones().get("ears");
            GeoBone tail = this.model.getAllBones().get("tail");
            if (ears != null) {
                this.earsPositionY = ears.getPosition().y;
                this.earsRotationX = ears.getRotation().x;
                this.earsRotationY = ears.getRotation().y;
                this.earsRotationZ = ears.getRotation().z;
            }
            if (tail != null) {
                this.tailPositionY = tail.getPosition().y;
                this.tailPositionZ = tail.getPosition().z;
                this.tailRotationX = tail.getRotation().x;
                this.tailRotationY = tail.getRotation().y;
                this.tailRotationZ = tail.getRotation().z;
            }
        }

        GeoBone ears = this.model.getAllBones().get("ears");
        GeoBone tail = this.model.getAllBones().get("tail");
        if (ears == null || tail == null) return;

        ModelPlayer playerModel = this.playerRenderer.getMainModel();
        ears.getRotation().set(
                this.earsRotationX - playerModel.bipedHead.rotateAngleX,
                this.earsRotationY - playerModel.bipedHead.rotateAngleY,
                this.earsRotationZ + playerModel.bipedHead.rotateAngleZ
        );
        ears.getPosition().y = this.earsPositionY - playerModel.bipedHead.rotationPointY;

        tail.getRotation().set(
                this.tailRotationX - playerModel.bipedBody.rotateAngleX,
                this.tailRotationY - playerModel.bipedBody.rotateAngleY,
                this.tailRotationZ + playerModel.bipedBody.rotateAngleZ
        );
        float bodyRotationX = -playerModel.bipedBody.rotateAngleX;
        float tailOffsetFromBodyY = tail.getPivot().y - 24;
        float tailOffsetFromBodyZ = tail.getPivot().z;
        float bodyRotationCos = (float) Math.cos(bodyRotationX);
        float bodyRotationSin = (float) Math.sin(bodyRotationX);
        tail.getPosition().y = this.tailPositionY + tailOffsetFromBodyY * bodyRotationCos - tailOffsetFromBodyZ * bodyRotationSin - tailOffsetFromBodyY;
        tail.getPosition().z = this.tailPositionZ + tailOffsetFromBodyY * bodyRotationSin + tailOffsetFromBodyZ * bodyRotationCos - tailOffsetFromBodyZ;

        GlStateManager.pushMatrix();
        if (player.isSneaking()) GlStateManager.translate(0, 0.2f, 0);
        GlStateManager.translate(0, 1.501f, 0);
        GlStateManager.scale(-1, -1, 1);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.getTextureLocation(player));
        this.render(this.model, player, partialTicks, 1, 1, 1, 1);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }

    @Override
    public GeoModelProvider<AbstractClientPlayer> getGeoModelProvider() {
        return this.modelProvider;
    }
}
