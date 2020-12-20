package com.gluton.glutech.blocks;

import com.gluton.glutech.tileentity.EnergyCellTileEntity;
import com.gluton.glutech.util.RegistryHandler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * @author Gluton
 */
public class EnergyCellBlock extends MachineBlock {
	
//	private static final EnumMap<Direction, EnumProperty<EnergyIOMode>> IO_PROPERTIES;
//	
//	static {
//		IO_PROPERTIES = new EnumMap<>(Direction.class);
//		for (Direction direction : Direction.values()) {
//			IO_PROPERTIES.put(direction, EnumProperty.create(direction.name().toLowerCase(), EnergyIOMode.class));
//		}
//	}

	public EnergyCellBlock() {
		super();
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return RegistryHandler.ENERGY_CELL.get().create();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if (worldIn != null && !worldIn.isRemote()) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (!(tile instanceof EnergyCellTileEntity)) {
				return ActionResultType.SUCCESS;
			}
			EnergyCellTileEntity energyTile = (EnergyCellTileEntity) tile;
			
			if (player.isSneaking()) {
				energyTile.nextIOMode(hit.getFace());
				player.sendMessage(new StringTextComponent("Changed face to " + energyTile.energyIOConfig.get(hit.getFace()).name()), null);
			} else {
				StringTextComponent energyAmount = new StringTextComponent(
						"Energy Stored: " + energyTile.getEnergyStored() + "/" + energyTile.getMaxEnergyStored());
				player.sendStatusMessage(energyAmount, true);
			}
			energyTile.markDirty();
			worldIn.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.BLOCK_UPDATE);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.hasTileEntity() && state.getBlock() != newState.getBlock()) {
			worldIn.removeTileEntity(pos);
		}
	}
	
//	@Override
//	public VoxelShape getRenderShape(BlockState state, IBlockReader reader, BlockPos pos) {
//		return VoxelShapes.fullCube();
//	}
//	
//	@Override
//	public BlockRenderType getRenderType(BlockState state) {
//		return BlockRenderType.MODEL;
//	}
	
//	@Override
//	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
//		super.fillStateContainer(builder);
//		for (EnumProperty<EnergyIOMode> property : IO_PROPERTIES.values()) {
//			builder.add(property);
//		}
//	}
}
