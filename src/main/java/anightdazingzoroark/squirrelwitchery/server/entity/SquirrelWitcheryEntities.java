package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.riftlib.model.ServerModelRegistry;
import anightdazingzoroark.squirrelwitchery.SquirrelWitchery;
import anightdazingzoroark.squirrelwitchery.server.entity.model.SquirrelGunModel;
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

        ResourceLocation explosiveNutRegistryName = new ResourceLocation(SquirrelWitchery.MODID, "explosive_nut");
        ForgeRegistries.ENTITIES.register(EntityEntryBuilder.<ExplosiveNutProjectileEntity>create()
                .entity(ExplosiveNutProjectileEntity.class)
                .factory(ExplosiveNutProjectileEntity::new)
                .id(explosiveNutRegistryName, 2)
                .name(explosiveNutRegistryName.getPath())
                .tracker(64, 3, true)
                .build()
        );

        ResourceLocation squirrelGunRegistryName = new ResourceLocation(SquirrelWitchery.MODID, "squirrel_with_gun");
        ServerModelRegistry.registerServerModel(SquirrelGunEntity.class, SquirrelGunModel::new);
        ForgeRegistries.ENTITIES.register(EntityEntryBuilder.<SquirrelGunEntity>create()
                .entity(SquirrelGunEntity.class)
                .factory(SquirrelGunEntity::new)
                .id(squirrelGunRegistryName, 3)
                .name(squirrelGunRegistryName.getPath())
                .tracker(64, 3, true)
                .spawn(EnumCreatureType.MONSTER, 2, 1, 1, BiomeDictionary.getBiomes(BiomeDictionary.Type.FOREST))
                .egg(0x6A4932, 0xC5AD91)
                .build()
        );

        ResourceLocation magicalSquirrelRegistryName = new ResourceLocation(SquirrelWitchery.MODID, "magical_squirrel");
        ForgeRegistries.ENTITIES.register(EntityEntryBuilder.<MagicalSquirrelEntity>create()
                .entity(MagicalSquirrelEntity.class)
                .factory(MagicalSquirrelEntity::new)
                .id(magicalSquirrelRegistryName, 4)
                .name(magicalSquirrelRegistryName.getPath())
                .tracker(64, 3, true)
                //.spawn(EnumCreatureType.MONSTER, 2, 1, 1, BiomeDictionary.getBiomes(BiomeDictionary.Type.FOREST))
                .egg(0xF0B2B9, 0xF19AA4)
                .build()
        );
    }
}