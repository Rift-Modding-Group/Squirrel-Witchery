package anightdazingzoroark.squirrelwitchery;

import net.minecraftforge.common.config.Config;

@Config(modid = SquirrelWitchery.MODID)
public class SquirrelWitcheryConfig {
    @Config.Name("Nut Drop Rate")
    @Config.Comment({
            "1/x chance for which a nut will drop by breaking leaves.",
            "Setting to 0 disables nuts dropping from leaves entirely."
    })
    public static int nutDropRate = 8;

    @Config.Name("Nut Drop Quantity Range")
    @Config.Comment("Upper and lower limit for amount of nuts that can drop from breaking leaves.")
    public static int[] nutDropQuantity = new int[]{2, 3};

    @Config.Name("Big Nut Drop Rate")
    @Config.Comment({
            "1/x chance for which a big nut will drop by breaking leaves.",
            "Setting to 0 disables big nuts dropping from leaves entirely."
    })
    public static int bigNutDropRate = 16;

    @Config.Name("Big Nut Drop Quantity Range")
    @Config.Comment("Upper and lower limit for amount of nuts that can drop from breaking leaves.")
    public static int[] bigNutDropQuantity = new int[]{1, 1};
}
