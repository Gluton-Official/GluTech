package com.gluton.glutech.blocks;

import java.util.Random;

import com.gluton.glutech.tileentity.SintererTileEntity;
import com.gluton.glutech.util.MachineItemHandler;
import com.gluton.glutech.util.RegistryHandler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * @author Gluton
 */
public class SintererBlock extends MachineBlock {

	public SintererBlock() {
		super();
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return RegistryHandler.SINTERER.get().create();
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		if (stack.hasDisplayName()) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof SintererTileEntity) {
				((SintererTileEntity) tile).setCustomName(stack.getDisplayName());
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
		if (worldIn != null && !worldIn.isRemote()) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof SintererTileEntity) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tile, pos);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof SintererTileEntity && state.getBlock() != newState.getBlock()) {
			SintererTileEntity sinterer = (SintererTileEntity) tile;
			((MachineItemHandler) sinterer.getInventory()).toNonNullList().forEach(item -> {
				ItemEntity itemEntity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), item);
				worldIn.addEntity(itemEntity);
			});
		}
		
		if (state.hasTileEntity() && state.getBlock() != newState.getBlock()) {
			worldIn.removeTileEntity(pos);
		}
	}
}
