package com.gluton.glutech.tileentity;

import com.gluton.glutech.blocks.MachineBlock;
import com.gluton.glutech.container.CrusherContainer;
import com.gluton.glutech.recipes.CrusherRecipe;
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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * @author Gluton
 */
public class CrusherTileEntity extends MachineTileEntity<CrusherRecipe> implements IEnergyStorage {
	
	public int energy;
	public int capacity;
	public int maxReceive;
	public int usageRate;
	
	public CrusherTileEntity() {
		super(RegistryHandler.CRUSHER.get(), "crusher", CrusherContainer.SLOTS, 100);
		
		this.capacity = 10000;
		this.maxReceive = 1000;
		
		this.usageRate = 20;
	}
	
	@Override
	public Container createMenu(final int windowId, final PlayerInventory playerInv, final PlayerEntity playerIn) {
		return new CrusherContainer(windowId, playerInv, this);
	}
	
	@Override
	public void tick() {
		boolean dirty = false;
		
		if (this.world != null && !this.world.isRemote) {
			if (this.energy >= this.usageRate) {
				CrusherRecipe recipe = this.getRecipe(this.inventory.getStackInSlot(0));
				if (recipe != null && outputAvailable(recipe, this.inventory.getStackInSlot(1))) {
					if (this.currentProcessTime < this.maxProcessTime) {
						this.world.setBlockState(this.getPos(), this.getBlockState().with(MachineBlock.ON, true));
						this.currentProcessTime++;
						this.energy -= this.usageRate;
						dirty = true;
					} else {
						this.world.setBlockState(this.getPos(), this.getBlockState().with(MachineBlock.ON, false));
						this.currentProcessTime = 0;
						ItemStack output = this.getRecipe(this.inventory.getStackInSlot(0)).getRecipeOutput();
						this.inventory.insertItem(1, output.copy(), false);
						this.inventory.decrStackSize(0, 1);
						dirty = true;
					}
				} else if (this.currentProcessTime != 0) {
					this.currentProcessTime = 0;
					this.world.setBlockState(this.getPos(), this.getBlockState().with(MachineBlock.ON, false));
				}
			} else if (this.currentProcessTime != 0) {
				this.currentProcessTime = 0;
				this.world.setBlockState(this.getPos(), this.getBlockState().with(MachineBlock.ON, false));
			}
		}
		
		if (dirty) {
			this.markDirty();
			this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
		}
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!canReceive()) {
			return 0;
		}
		
		int energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, maxReceive));
		if (!simulate) {
			this.energy += energyReceived;
			this.markDirty();
			this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
		}
		return energyReceived;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}
	
	@Override
	public int getEnergyStored() {
		return this.energy;
	}
	
	@Override
	public int getMaxEnergyStored() {
		return this.capacity;
	}
	
	@Override
	public boolean canExtract() {
		return false;
	}
	
	@Override
	public boolean canReceive() {
		return true;
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
		nbt.putInt("Energy", this.energy);
		
		return nbt;
	}

	@Override
	protected IRecipeType<MachineRecipe> getRecipeType() {
		return RegistryHandler.CRUSHER_RECIPE_TYPE;
	}
}
