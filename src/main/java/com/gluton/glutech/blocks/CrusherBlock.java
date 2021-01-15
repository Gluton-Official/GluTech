package com.gluton.glutech.blocks;

import java.util.List;
import java.util.Random;

import com.gluton.glutech.registry.Registry;
import com.gluton.glutech.tileentity.CrusherTileEntity;
import com.gluton.glutech.util.EnergyFormat;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * @author Gluton
 */
public class CrusherBlock extends MachineBlock {
	
	public CrusherBlock() {
		super();
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return Registry.CRUSHER.getTileEntity();
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		if (stack.hasDisplayName()) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof CrusherTileEntity) {
				((CrusherTileEntity) tile).setCustomName(stack.getDisplayName());
			}
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (stateIn.get(ON)) {
			// Sound
			double x = (double) pos.getX() + 0.5D;
			double y = (double) pos.getY();
			double z = (double) pos.getZ() + 0.5D;
			if (rand.nextDouble() < 0.1D) {
				worldIn.playSound(x, y, z, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}

			// Particles
			Direction direction = stateIn.get(FACING);
			Direction.Axis axis = direction.getAxis();
			double lateralOffset = rand.nextDouble() * 0.6D - 0.3D;
			double xOffset = axis == Direction.Axis.X ? (double) direction.getXOffset() * 0.52D : lateralOffset;
			double yOffset = rand.nextDouble() * 6.0D / 16.0D;
			double zOffset = axis == Direction.Axis.Z ? (double) direction.getZOffset() * 0.52D : lateralOffset;
			worldIn.addParticle(ParticleTypes.SMOKE, x + xOffset, y + yOffset, z + zOffset, 0.0D, 0.0D, 0.0D);
			worldIn.addParticle(ParticleTypes.FLAME, x + xOffset, y + yOffset, z + zOffset, 0.0D, 0.0D, 0.0D);
		}
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if (worldIn == null) {
			return ActionResultType.FAIL;
		}
		
		if (!worldIn.isRemote()) {
			CrusherTileEntity tile = getTileEntity(worldIn, pos);
			if (tile != null) {
				NetworkHooks.openGui((ServerPlayerEntity) player, tile, pos);
			}
		}
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (worldIn != null && !worldIn.isRemote() && !player.isCreative()) {
			CrusherTileEntity tile = getTileEntity(worldIn, pos);
			if (tile != null) {
				ItemStack itemStack = new ItemStack(Registry.CRUSHER.getBlock());
				
				CompoundNBT nbt = tile.saveToNBT(new CompoundNBT());
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
		if (state.getBlock() != newState.getBlock()) {
//			CrusherTileEntity tile = getTileEntity(worldIn, pos);
//			if (tile != null) {
//				((MachineItemHandler) tile.getInventory()).toNonNullList().forEach(item -> {
//					ItemEntity itemEntity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), item);
//					worldIn.addEntity(itemEntity);
//				});
//			}
			if (state.hasTileEntity()) {
				worldIn.removeTileEntity(pos);
			}
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
				energy, CrusherTileEntity.CAPACITY, EnergyFormat.COMPACT, TextFormatting.GRAY, false)));
	}
}
