package anightdazingzoroark.squirrelwitchery.mixin;

import anightdazingzoroark.squirrelwitchery.SquirrelWitcheryUtils;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.gui.GuiArcaneWorkbench;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

@Mixin(value = GuiArcaneWorkbench.class, remap = false)
public abstract class GuiArcaneWorkbenchMixin {
    @Shadow
    private TileArcaneWorkbench tileEntity;

    /**
     * Allow risunium crystal to be used in the ui
     * */
    @Redirect(
            method = "drawGuiContainerBackgroundLayer",
            at = @At(value = "INVOKE", target = "Lthaumcraft/common/blocks/world/ore/ShardType;getMetaByAspect(Lthaumcraft/api/aspects/Aspect;)I"),
            require = 1
    )
    private int resolveCrystalSlot(Aspect aspect) {
        int shardMetadata = ShardType.getMetaByAspect(aspect);
        if (shardMetadata >= 0) return shardMetadata;

        for (int slot = 0; slot < 6; slot++) {
            ItemStack slottedCrystal = this.tileEntity.inventoryCraft.getStackInSlot(9 + slot);
            if (SquirrelWitcheryUtils.isRisuniumCrystal(slottedCrystal)) return slot;
        }

        return 0;
    }
}
