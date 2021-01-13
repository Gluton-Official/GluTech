package com.gluton.glutech.tileentity;

import javax.annotation.Nullable;

import com.gluton.glutech.blocks.MachineBlock;
import com.gluton.glutech.blocks.properties.EnergyIOMode;
import com.gluton.glutech.container.FurnaceGeneratorContainer;
import com.gluton.glutech.container.IContainer;
import com.gluton.glutech.registry.Registry;
import com.gluton.glutech.util.MachineItemHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 * @author Gluton
 */
public class FurnaceGeneratorTileEntity extends MachineTileEntity implements IContainer<FurnaceGeneratorContainer> {
	
	private int powerRate;
	private int remainingBurnTime;
	private int fuelBurnTime;
	private ITextComponent customName;
	private MachineItemHandler inventory;

	public FurnaceGeneratorTileEntity() {
		super(Registry.FURNACE_GENERATOR.getTileEntityType(), "furnace_generator", 0, 10000, 0, 1000);
		
		this.powerRate = 15;
		this.fuelBurnTime = 0;
		
		this.inventory = this.createInventory(FurnaceGeneratorContainer.SLOTS);
		
		this.capabilities.put(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, new CapabilityCallable<IItemHandler>(this.inventory) {
			@Override
			public LazyOptional<IItemHandler> side(Direction side) {
				return LazyOptional.of(() -> this.storage); 
			}
		});
	}

	@Override
	public boolean machineTick() {
		boolean dirty = false;
		
		if (this.remainingBurnTime > 0) {
			if (this.energy + powerRate <= capacity) {
				this.remainingBurnTime--;
				this.energy += powerRate;
				dirty = true;
				// TODO: fix this property state not being set every tick
				setPropertyState(MachineBlock.ON, true);
			} else {
				setPropertyState(MachineBlock.ON, false);
			}
		} else if (!this.inventory.getStackInSlot(0).isEmpty()) {
			this.fuelBurnTime = ForgeHooks.getBurnTime(this.inventory.getStackInSlot(0));
			this.remainingBurnTime = this.fuelBurnTime;
			this.inventory.decrStackSize(0, 1);
			dirty = true;
		} else if (this.fuelBurnTime != 0) {
			this.fuelBurnTime = 0;
			setPropertyState(MachineBlock.ON, false);
			dirty = true;
		}
		
		if (this.energy > 0) {
			dirty |= transferEnergy();
		}
		
		return dirty;
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
	public EnergyIOMode getEnergyIOModeForSide(Direction side) {
		return EnergyIOMode.OUTPUT;
	}
	
	@Override
	public FurnaceGeneratorContainer createMenu(final int windowId, final PlayerInventory playerInv, final PlayerEntity playerIn) {
		return new FurnaceGeneratorContainer(windowId, playerInv, this);
	}
	
	@Override
	public void loadFromNBT(CompoundNBT nbt) {
		readCustomNameFromNBT(nbt);
		
		this.inventory.setNonNullList(readInventoryFromNBT(nbt, this.inventory.getSlots()));
		
		this.remainingBurnTime = nbt.getInt("RemainingBurnTime");
		this.fuelBurnTime = nbt.getInt("FuelBurnTime");
		this.energy = nbt.getInt("Energy");
	}

	@Override
	public CompoundNBT saveToNBT(CompoundNBT nbt) {
		nbt = writeCustomNameToNBT(nbt);
		
		nbt = writeInventoryToNBT(nbt);
		
		nbt.putInt("RemainingBurnTime", this.remainingBurnTime);
		nbt.putInt("FuelBurnTime", this.fuelBurnTime);
		nbt.putInt("Energy", this.energy);
		
		return nbt;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public void setCustomName(ITextComponent customName) {
		this.customName = customName;
	}
	
	@Nullable
	@Override
	public ITextComponent getCustomName() {
		return this.customName;
	}
	
	@Override
	public final ItemStackHandler getInventory() {
		return this.inventory;
	}
	
	public void setRemainingBurnTime(int remainingBurnTime) {
		this.remainingBurnTime = remainingBurnTime;
	}
	
	public int getRemainingBurnTime() {
		return this.remainingBurnTime;
	}
	
	public void setFuelBurnTime(int fuelBurnTime) {
		this.fuelBurnTime = fuelBurnTime;
	}
	
	public int getFuelBurnTime() {
		return this.fuelBurnTime;
	}
}
