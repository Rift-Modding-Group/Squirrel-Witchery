package anightdazingzoroark.squirrelwitchery.server.research.theorycraft;

import anightdazingzoroark.squirrelwitchery.server.SquirrelWitcheryResearch;
import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardRisunicResonance extends TheorycraftCard {
    public CardRisunicResonance() {}

    @Override
    public boolean initialize(EntityPlayer player, ResearchTableData data) {
        return ThaumcraftCapabilities.knowsResearchStrict(player, SquirrelWitcheryResearch.VIS_CRYSTAL_RISUNIUM);
    }

    @Override
    public int getInspirationCost() {
        return 2;
    }

    @Override
    public String getResearchCategory() {
        return SquirrelWitcheryResearch.CATEGORY;
    }

    @Override
    public String getLocalizedName() {
        return new TextComponentTranslation("card.risunic_resonance.name").getFormattedText();
    }

    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.risunic_resonance.text").getFormattedText();
    }

    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[]{ThaumcraftApiHelper.makeCrystal(SquirrelWitcheryAspects.RISUNIUM)};
    }

    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[]{true};
    }

    @Override
    public boolean activate(EntityPlayer player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 30);
        data.bonusDraws++;
        return true;
    }
}
