package com.gluton.glutech.tileentity;

import javax.annotation.Nullable;

import com.gluton.glutech.blocks.MachineBlock;
import com.gluton.glutech.blocks.properties.EnergyIOMode;
import com.gluton.glutech.container.FurnaceGeneratorContainer;
import com.gluton.glutech.container.IContainer;
import com.gluton.glutech.registry.Registry;
import com.gluton.glutech.util.MachineItemHandler;
import com.gluton.glutech.util.NBTUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * @author Gluton
 */
public class FurnaceGeneratorTileEntity extends MachineTileEntity implements IContainer<FurnaceGeneratorContainer> {
	
	private int powerRate;
	private int remainingBurnTime;
	private int fuelBurnTime;
	private ITextComponent customName;
	private MachineItemHandler inventory;
	
	public static final int BASE_ENERGY = 0;
	public static final int CAPACITY = 10000;
	public static final int TRANSFER_IN = 0;
	public static final int TRANSFER_OUT = 1000;

	public FurnaceGeneratorTileEntity() {
		super(Registry.FURNACE_GENERATOR.getTileEntityType(), "furnace_generator", BASE_ENERGY, CAPACITY, TRANSFER_IN, TRANSFER_OUT);
		
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
		super.loadFromNBT(nbt);
		
		readCustomNameFromNBT(nbt);
		
		this.inventory.setNonNullList(readInventoryFromNBT(nbt, this.inventory.getSlots()));
		
		this.remainingBurnTime = nbt.getInt("RemainingBurnTime");
		this.fuelBurnTime = nbt.getInt("FuelBurnTime");
	}

	@Override
	public CompoundNBT saveToNBT(CompoundNBT nbt) {
		nbt = super.saveToNBT(nbt);
		
		nbt = writeCustomNameToNBT(nbt);
		nbt = writeInventoryToNBT(nbt);
		
		NBTUtils.putOptionalInt(nbt, "RemainingBurnTime", this.remainingBurnTime, 0);
		NBTUtils.putOptionalInt(nbt, "FuelBurnTime", this.fuelBurnTime, 0);
		
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
	public final MachineItemHandler getInventory() {
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
