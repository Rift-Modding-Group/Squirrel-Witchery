package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SquirrelWitcheryEntities {
    public static void registerEntities() {
        ResourceLocation registryName = new ResourceLocation(SquirrelWitchery.MODID, "squirrel");
        ForgeRegistries.ENTITIES.register(EntityEntryBuilder.<SquirrelEntity>create()
                .entity(SquirrelEntity.class)
                .factory(SquirrelEntity::new)
                .id(registryName, 0)
                .name(registryName.getPath())
                .tracker(64, 3, true)
                .spawn(EnumCreatureType.CREATURE, 10, 2, 4, BiomeDictionary.getBiomes(BiomeDictionary.Type.FOREST))
                .egg(0x6A4932, 0xC5AD91)
                .build()
        );
    }
}
