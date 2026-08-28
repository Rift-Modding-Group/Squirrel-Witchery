package anightdazingzoroark.squirrelwitchery.server.research.theorycraft;

import anightdazingzoroark.squirrelwitchery.server.SquirrelWitcheryResearch;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardNutResearch extends TheorycraftCard {
    public CardNutResearch() {}

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
        return new TextComponentTranslation("card.nut_research.name").getFormattedText();
    }

    @Override
    public String getLocalizedText() {
        return new TextComponentTranslation("card.nut_research.text").getFormattedText();
    }

    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[]{new ItemStack(SquirrelWitcheryItems.NUT)};
    }

    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[]{true};
    }

    @Override
    public boolean activate(EntityPlayer player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), 25);
        return true;
    }
}
