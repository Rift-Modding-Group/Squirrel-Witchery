package anightdazingzoroark.squirrelwitchery.server.items;

import net.minecraft.item.Item;

public class NutsaberItem extends Item implements IRisuniumConsumer {
    public NutsaberItem() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getMaxRisunium() {
        return 250;
    }
}
