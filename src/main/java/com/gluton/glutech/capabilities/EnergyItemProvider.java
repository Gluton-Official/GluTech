package com.gluton.glutech.capabilities;

import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * @author Gluton
 */
public class EnergyItemProvider implements ICapabilityProvider {
	
	private LazyOptional<IEnergyStorage> capabilityItem;
	
	public EnergyItemProvider(final ItemStack itemStack, Consumer<ItemStack> onEnergyUpdate,
			Function<ItemStack, CompoundNBT> energyTagPath, int capacity, boolean canExtract, boolean canReceive) {	
		capabilityItem = LazyOptional.of(
				() -> new EnergyItem(itemStack, onEnergyUpdate, energyTagPath, capacity, canExtract, canReceive));
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == CapabilityEnergy.ENERGY ? capabilityItem.cast() : LazyOptional.empty();
	}
	
	private class EnergyItem implements IEnergyStorage {
		
		private ItemStack itemStack;
		private Consumer<ItemStack> onEnergyUpdate;
		private Function<ItemStack, CompoundNBT> energyTagPath;
		private int energy;
		private final int capacity;
		private final boolean canExtract;
		private final boolean canReceive;
		
		public EnergyItem(ItemStack itemStack, Consumer<ItemStack> onEnergyUpdate, 
				Function<ItemStack, CompoundNBT> energyTagPath, int capacity, boolean canExtract, boolean canReceive) {
			this.itemStack = itemStack;
			this.onEnergyUpdate = onEnergyUpdate;
			this.energyTagPath = energyTagPath;
			this.energy = this.energyTagPath.apply(this.itemStack).getInt("Energy");
			this.capacity = capacity;
			this.canExtract = canExtract;
			this.canReceive = canReceive;
		}
		
		protected void updateEnergy(int energy) {
			this.energyTagPath.apply(this.itemStack).putInt("Energy", energy);
			this.onEnergyUpdate.accept(this.itemStack);
		}
		
		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			if (!canReceive()) {
				return 0;
			}
			
			int energyReceived = Math.min(this.capacity - this.energy, Math.min(this.capacity, maxReceive));
			if (!simulate && energyReceived > 0) {
				this.energy += energyReceived;
				updateEnergy(this.energy);
			}
			return energyReceived;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			if (!canExtract()) {
				return 0;
			}
			
			int energyExtracted = Math.min(this.energy, Math.min(this.capacity, maxExtract));
			if (!simulate && energyExtracted > 0) {
				this.energy -= energyExtracted;
				updateEnergy(this.energy);
			}
			return energyExtracted;
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
			return this.canExtract;
		}

		@Override
		public boolean canReceive() {
			return this.canReceive;
		}
	}
}
