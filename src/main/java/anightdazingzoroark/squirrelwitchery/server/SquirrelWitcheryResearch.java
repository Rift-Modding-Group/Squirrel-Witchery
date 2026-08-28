package anightdazingzoroark.squirrelwitchery.server;

import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;

public final class SquirrelWitcheryResearch {
    public static final String CATEGORY = "SQUIRREL_WITCHERY";
    public static final String FOUND_NUT = "SQUIRREL_WITCHERY_FOUND_NUT";

    public static void registerCategory() {
        ResearchCategories.registerCategory(
                CATEGORY, FOUND_NUT,
                new AspectList(),
                new ResourceLocation(SquirrelWitchery.MODID, "textures/items/witch_hat.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_1.jpg"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png")
        );
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(
                SquirrelWitchery.MODID,
                "research/squirrel_witchery"
        ));
    }
}
