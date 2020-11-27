package com.gluton.glutech.tileentity;

import com.gluton.glutech.blocks.FurnaceGeneratorBlock;
import com.gluton.glutech.container.FurnaceGeneratorContainer;
import com.gluton.glutech.recipes.MachineRecipe;
import com.gluton.glutech.util.RegistryHandler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * @author Gluton
 */
// Uses MachineRecipe as a default recipe, since no recipes are needed.
public class FurnaceGeneratorTileEntity extends MachineTileEntity<MachineRecipe> implements IEnergyStorage {
	
	public int fuelBurnTime;
	public int energy;
	public int capacity;
	public int maxExtract;
	public final int powerRate = 15;

	public FurnaceGeneratorTileEntity() {
		// Uses maxProcessTime of 0 to ignore maximum
		super(RegistryHandler.FURNACE_GENERATOR.get(), "furance_generator", FurnaceGeneratorContainer.SLOTS, 0);
		
		this.fuelBurnTime = 0;
		
		this.capacity = 32000;
		this.maxExtract = 1000;
	}
	
	@Override
	public Container createMenu(final int windowId, final PlayerInventory playerInv, final PlayerEntity playerIn) {
		return new FurnaceGeneratorContainer(windowId, playerInv, this);
	}

	@Override
	public void tick() {
		boolean dirty = false;
		
		if (this.world != null && !this.world.isRemote) {
			if (this.currentProcessTime > 0) {
				if (this.energy + powerRate <= capacity) {
					this.currentProcessTime--;
					this.energy += powerRate;
					dirty = true;
				} else {
					this.world.setBlockState(this.getPos(), this.getBlockState().with(FurnaceGeneratorBlock.ON, false));
				}
			} else if (!this.inventory.getStackInSlot(0).isEmpty()) {
				this.fuelBurnTime = ForgeHooks.getBurnTime(this.inventory.getStackInSlot(0));
				this.currentProcessTime = this.fuelBurnTime;
				this.inventory.decrStackSize(0, 1);
				this.world.setBlockState(this.getPos(), this.getBlockState().with(FurnaceGeneratorBlock.ON, true));
				dirty = true;
				
			} else if (this.fuelBurnTime != 0) {
				this.fuelBurnTime = 0;
				this.world.setBlockState(this.getPos(), this.getBlockState().with(FurnaceGeneratorBlock.ON, false));
				dirty = true;
			}
		}
		
		if (dirty) {
			this.markDirty();
			this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
		}
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!canExtract()) {
			return 0;
		}
		
		int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
		if (!simulate) {
			energy -= energyExtracted;
		}
		return energyExtracted;
	}
	
	@Override
	public int getEnergyStored() {
		return energy;
	}
	
	@Override
	public int getMaxEnergyStored() {
		return capacity;
	}
	
	@Override
	public boolean canExtract() {
		return true;
	}
	
	@Override
	public boolean canReceive() {
		return false;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		if (nbt.contains("CustomName", Constants.NBT.TAG_STRING)) {
			this.customName = ITextComponent.Serializer.getComponentFromJson(nbt.getString("CustomName"));
		}
		
		NonNullList<ItemStack> inv = NonNullList.<ItemStack>withSize(this.inventory.getSlots(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(nbt, inv);
		this.inventory.setNonNullList(inv);
		
		this.currentProcessTime = nbt.getInt("CurrentProcessTime");
		this.fuelBurnTime = nbt.getInt("FuelBurnTime");
		this.energy = nbt.getInt("Energy");
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		if (this.customName != null) {
			nbt.putString("CustomName", ITextComponent.Serializer.toJson(customName));
		}
		
		ItemStackHelper.saveAllItems(nbt, this.inventory.toNonNullList());
		
		nbt.putInt("CurrentProcessTime", this.currentProcessTime);
		nbt.putInt("FuelBurnTime", this.fuelBurnTime);
		nbt.putInt("Energy", this.energy);
		
		return nbt;
	}

	@Override
	protected MachineRecipe getRecipe(ItemStack ...stacks) {
		return null;
	}

	@Override
	protected IRecipeType<MachineRecipe> getRecipeType() {
		return null;
	}
}
