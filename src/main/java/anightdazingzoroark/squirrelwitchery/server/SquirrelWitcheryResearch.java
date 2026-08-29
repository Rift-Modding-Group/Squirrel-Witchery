package anightdazingzoroark.squirrelwitchery.server;

import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.research.theorycraft.CardNutResearch;
import anightdazingzoroark.squirrelwitchery.server.research.theorycraft.CardRisunicResonance;
import anightdazingzoroark.squirrelwitchery.server.research.theorycraft.CardSquirrelFieldNotes;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.theorycraft.TheorycraftManager;

public final class SquirrelWitcheryResearch {
    public static final String CATEGORY = "SQUIRREL_WITCHERY";
    public static final String FOUND_NUT = "SQUIRREL_WITCHERY_FOUND_NUT";
    public static final String VIS_CRYSTAL_RISUNIUM = "SQUIRREL_WITCHERY_VIS_CRYSTAL_RISUNIUM";
    public static final String RISUNIC_STUDIES = "SQUIRREL_WITCHERY_RISUNIC_STUDIES";
    public static final String CRYSTAL_RISUNIUM = "SQUIRREL_WITCHERY_CRYSTAL_RISUNIUM";
    public static final String WITCH_COSTUME = "SQUIRREL_WITCHERY_WITCH_COSTUME";
    public static final String DARK_WITCH_COSTUME = "SQUIRREL_WITCHERY_DARK_WITCH_COSTUME";

    public static void registerCategory() {
        ResearchCategories.registerCategory(
                CATEGORY, FOUND_NUT,
                new AspectList().add(SquirrelWitcheryAspects.RISUNIUM, 15),
                new ResourceLocation(SquirrelWitchery.MODID, "textures/items/witch_hat.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_1.jpg"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png")
        );
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(
                SquirrelWitchery.MODID,
                "research/squirrel_witchery"
        ));
        TheorycraftManager.registerCard(CardSquirrelFieldNotes.class);
        TheorycraftManager.registerCard(CardNutResearch.class);
        TheorycraftManager.registerCard(CardRisunicResonance.class);
    }
}
