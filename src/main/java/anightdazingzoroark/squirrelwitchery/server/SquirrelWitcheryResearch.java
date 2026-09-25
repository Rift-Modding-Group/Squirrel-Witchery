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

import java.util.List;

public final class SquirrelWitcheryResearch {
    public static final String CATEGORY = "SQUIRREL_WITCHERY";
    public static final String SQUIRREL = "!squirrel";
    public static final String CRYSTALLIZED_SQUIRREL_HEART = "SQUIRREL_WITCHERY_CRYSTALLIZED_SQUIRREL_HEART";
    public static final String SQUIRREL_RISUNIUM_INTERACTION = "squirrel_risunium";
    public static final String SQUIRREL_HEART_USED = "squirrel_heart_used";
    public static final String NUTS = "SQUIRREL_WITCHERY_NUTS";
    public static final String RISUNIUM_DISCOVERED = "SQUIRREL_WITCHERY_RISUNIUM_DISCOVERED";
    public static final String RISUNIC_DEVICES = "SQUIRREL_WITCHERY_RISUNIC_DEVICES";
    public static final String WITCH_STAFF = "SQUIRREL_WITCHERY_WITCH_STAFF";
    public static final String WITCH_STAFF_ATTACHMENTS = "SQUIRREL_WITCHERY_WITCH_STAFF_ATTACHMENTS";
    public static final String WITCH_SHOTGUN = "SQUIRREL_WITCHERY_WITCH_SHOTGUN";
    public static final String NUTSABER = "SQUIRREL_WITCHERY_NUTSABER";
    public static final String WITCH_COSTUME = "SQUIRREL_WITCHERY_WITCH_COSTUME";
    public static final String DARK_WITCH_COSTUME = "SQUIRREL_WITCHERY_DARK_WITCH_COSTUME";
    public static final String WITCH_BROOM = "SQUIRREL_WITCHERY_WITCH_BROOM";
    public static final String WITCH_PENDANT = "SQUIRREL_WITCHERY_WITCH_PENDANT";
    public static final List<String> RISUNIC_DEVICE_TABS = List.of(WITCH_BROOM, WITCH_STAFF, WITCH_SHOTGUN, NUTSABER);

    public static void registerCategory() {
        ResearchCategories.registerCategory(
                CATEGORY, RISUNIUM_DISCOVERED,
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
