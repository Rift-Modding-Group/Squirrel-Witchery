package anightdazingzoroark.squirrelwitchery.server.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SquirrelPlushBlock extends Block {
    public static PropertyDirection FACING = BlockHorizontal.FACING;

    public SquirrelPlushBlock() {
        super(Material.CLOTH);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setSoundType(SoundType.CLOTH);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (worldIn.isRemote) return;

        IBlockState blockStateNorth = worldIn.getBlockState(pos.north());
        IBlockState blockStateSouth = worldIn.getBlockState(pos.south());
        IBlockState blockStateEast = worldIn.getBlockState(pos.west());
        IBlockState blockStateWest = worldIn.getBlockState(pos.east());

        EnumFacing enumfacing = state.getValue(FACING);
        if (enumfacing == EnumFacing.NORTH && blockStateNorth.isFullBlock() && !blockStateSouth.isFullBlock()) {
            enumfacing = EnumFacing.SOUTH;
        }
        else if (enumfacing == EnumFacing.SOUTH && blockStateSouth.isFullBlock() && !blockStateNorth.isFullBlock()) {
            enumfacing = EnumFacing.NORTH;
        }
        else if (enumfacing == EnumFacing.WEST && blockStateEast.isFullBlock() && !blockStateWest.isFullBlock()) {
            enumfacing = EnumFacing.EAST;
        }
        else if (enumfacing == EnumFacing.EAST && blockStateWest.isFullBlock() && !blockStateEast.isFullBlock()) {
            enumfacing = EnumFacing.WEST;
        }

        worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
    }

    @Override
    public IBlockState getStateForPlacement(
            World worldIn, BlockPos pos, EnumFacing facing,
            float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer
    ) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rotation) {
        return state.withProperty(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
}
