package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SquirrelWitcheryEntities {
    public static void registerEntities() {
        ResourceLocation squirrelRegistryName = new ResourceLocation(SquirrelWitchery.MODID, "squirrel");
        ForgeRegistries.ENTITIES.register(EntityEntryBuilder.<SquirrelEntity>create()
                .entity(SquirrelEntity.class)
                .factory(SquirrelEntity::new)
                .id(squirrelRegistryName, 0)
                .name(squirrelRegistryName.getPath())
                .tracker(64, 3, true)
                .spawn(EnumCreatureType.CREATURE, 10, 2, 4, BiomeDictionary.getBiomes(BiomeDictionary.Type.FOREST))
                .egg(0x6A4932, 0xC5AD91)
                .build()
        );

        ResourceLocation witchBroomRegistryName = new ResourceLocation(SquirrelWitchery.MODID, "witch_broom");
        ForgeRegistries.ENTITIES.register(EntityEntryBuilder.<WitchBroomEntity>create()
                .entity(WitchBroomEntity.class)
                .factory(WitchBroomEntity::new)
                .id(witchBroomRegistryName, 1)
                .name(witchBroomRegistryName.getPath())
                .tracker(64, 3, true)
                .build()
        );
    }
}
