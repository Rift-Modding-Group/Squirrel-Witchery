package anightdazingzoroark.squirrelwitchery.mixin;

import anightdazingzoroark.squirrelwitchery.server.SquirrelWitcheryResearch;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.client.gui.GuiResearchPage;

@Mixin(value = GuiResearchPage.class, remap = false)
public abstract class GuiResearchPageMixin {
    @Shadow
    private ResearchEntry research;

    //get le custom textures
    @Redirect(
            method = "drawRequirements",
            at = @At(
                    value = "INVOKE",
                    target = "Lthaumcraft/api/research/ResearchStage;getResearchIcon()[Ljava/lang/String;"
            ),
            require = 2
    )
    private String[] provideCustomMilestoneIcons(ResearchStage stage) {
        String[] researchIcons = stage.getResearchIcon();
        String[] requiredResearch = stage.getResearch();
        for (int index = 0; index < requiredResearch.length; index++) {
            if (requiredResearch[index].equals(SquirrelWitcheryResearch.SQUIRREL_RISUNIUM_INTERACTION)) {
                researchIcons[index] = "squirrelwitchery:textures/items/squirrel_fur.png";
            }
            else if (requiredResearch[index].equals(SquirrelWitcheryResearch.SQUIRREL_HEART_USED)) {
                researchIcons[index] = "squirrelwitchery:textures/items/crystallized_squirrel_heart.png";
            }
        }
        return researchIcons;
    }

    @Redirect(
            method = "drawRequirements",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;startsWith(Ljava/lang/String;)Z"),
            require = 4
    )
    private boolean setCustomIcon(String researchKey, String prefix) {
        if (researchKey.equals(SquirrelWitcheryResearch.SQUIRREL_RISUNIUM_INTERACTION) || researchKey.equals(SquirrelWitcheryResearch.SQUIRREL_HEART_USED)) {
            return false;
        }

        return researchKey.startsWith(prefix);
    }

    //fix that opengl leak
    @Redirect(
            method = "drawRequirements",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V",
                    ordinal = 2
            ),
            require = 1
    )
    private void fixCustomIconColor(float red, float green, float blue, float alpha) {
        if (this.research.getKey().equals(SquirrelWitcheryResearch.CRYSTALLIZED_SQUIRREL_HEART)) {
            GlStateManager.color(1f, 1f, 1f, 1f);
        }
        else GlStateManager.color(red, green, blue, alpha);
    }
}
