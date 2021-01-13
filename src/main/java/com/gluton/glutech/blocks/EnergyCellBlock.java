package com.gluton.glutech.blocks;

import java.util.List;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.registry.Registry;
import com.gluton.glutech.tileentity.EnergyCellTileEntity;
import com.gluton.glutech.util.EnergyFormat;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
		return Registry.ENERGY_CELL.getTileEntity();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		EnergyCellTileEntity energyCell = getTileEntity(worldIn, pos);
		if (energyCell == null) {
			return ActionResultType.FAIL;
		}
		
		if (!worldIn.isRemote()) {
			if (player.isSneaking() && player.inventory.getCurrentItem().isEmpty()) {
				energyCell.nextIOMode(hit.getFace());
				energyCell.markDirty();
				energyCell.notifyBlockUpdate(Constants.BlockFlags.BLOCK_UPDATE);
				energyCell.notifyBlockUpdate(Constants.BlockFlags.NOTIFY_NEIGHBORS);
			} else {
				StringTextComponent energyAmount = new StringTextComponent(EnergyFormat.getEnergyLabel(
						TextFormatting.GREEN + "Energy Stored", energyCell.getEnergyStored(),
						energyCell.getMaxEnergyStored(), EnergyFormat.COMPACT));
				player.sendStatusMessage(energyAmount, true);
			}
		}
		return ActionResultType.SUCCESS;
	}
	
	// TODO: make empty energy cells not drop with an nbt tag so they can be stacked
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (worldIn != null && !worldIn.isRemote() && !player.isCreative()) {
			EnergyCellTileEntity energyCellTileEntity = getTileEntity(worldIn, pos);
			if (energyCellTileEntity != null) {
				GluTech.LOGGER.info(energyCellTileEntity.getEnergyIOConfg().toString());
				ItemStack itemStack = new ItemStack(Registry.ENERGY_CELL.getBlock());
				
				CompoundNBT nbt = energyCellTileEntity.write(new CompoundNBT());
				if (!nbt.isEmpty()) {
					itemStack.setTagInfo("BlockEntityTag", nbt);
				}
				
				ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + 0.5D,
						(double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, itemStack);
	            itementity.setDefaultPickupDelay();
	            worldIn.addEntity(itementity);
			}
		}
		
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.hasTileEntity() && state.getBlock() != newState.getBlock()) {
			worldIn.removeTileEntity(pos);
			worldIn.notifyBlockUpdate(pos, state, state, Constants.BlockFlags.UPDATE_NEIGHBORS);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip,
			ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
		int energy = 0;
		if (nbt != null && nbt.contains("Energy")) {
			energy = nbt.getInt("Energy");
		}
		tooltip.add(new StringTextComponent(EnergyFormat.getEnergyLabel(TextFormatting.GREEN + "Energy",
				energy, EnergyCellTileEntity.CAPACITY, EnergyFormat.COMPACT, TextFormatting.GRAY, false)));
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
