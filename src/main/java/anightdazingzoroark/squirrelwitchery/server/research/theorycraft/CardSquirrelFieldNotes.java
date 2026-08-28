package anightdazingzoroark.squirrelwitchery.server.research.theorycraft;

import anightdazingzoroark.squirrelwitchery.server.SquirrelWitcheryResearch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardSquirrelFieldNotes extends TheorycraftCard {
    public CardSquirrelFieldNotes() {}

    @Override
    public boolean initialize(EntityPlayer player, ResearchTableData data) {
        return ThaumcraftCapabilities.knowsResearchStrict(player, SquirrelWitcheryResearch.VIS_CRYSTAL_RISUNIUM);
    }

    @Override
    public int getInspirationCost() {
        return 1;
    }

    @Override
    public String getResearchCategory() {
        return SquirrelWitcheryResearch.CATEGORY;
    }

    @Override
    public String getLocalizedName() {
        return new TextComponentTranslation("card.squirrel_field_notes.name").getFormattedText();
    }

    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.squirrel_field_notes.text").getFormattedText();
    }

    @Override
    public boolean activate(EntityPlayer player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 20);
        return true;
    }
}
