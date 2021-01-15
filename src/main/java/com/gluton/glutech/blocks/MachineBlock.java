package com.gluton.glutech.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.gluton.glutech.tileentity.MachineTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

/**
 * @author Gluton
 */
public abstract class MachineBlock extends Block {
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty ON = BooleanProperty.create("on");

	public MachineBlock() {
		super(Block.Properties.create(Material.IRON)
				.sound(SoundType.METAL)
				.hardnessAndResistance(3.0f, 7.0f)
				.harvestTool(ToolType.PICKAXE));
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(ON, false));
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);
	
	/**
	 * @return the TileEntity casted to T, or null if no TileEntity was at the position
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public <T extends MachineTileEntity> T getTileEntity(@Nonnull World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null) {
			return (T) tile;
		}
		return null;
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(FACING, ON);
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.with(FACING, mirror.toRotation(state.get(FACING)).rotate(state.get(FACING)));
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}
	
	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return state.get(ON) ? super.getLightValue(state, world, pos) : 0;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}
	
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		return Container.calcRedstone(worldIn.getTileEntity(pos));
	}
	
	@Override
	public abstract ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit);
	
	@Override
	public abstract void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving);
}
