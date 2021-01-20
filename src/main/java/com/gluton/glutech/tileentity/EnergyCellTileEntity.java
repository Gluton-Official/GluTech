package com.gluton.glutech.tileentity;

import java.util.EnumMap;

import com.gluton.glutech.blocks.MachineBlock;
import com.gluton.glutech.blocks.properties.EnergyIOMode;
import com.gluton.glutech.capabilities.CapabilityCallable;
import com.gluton.glutech.container.EnergyCellContainer;
import com.gluton.glutech.container.IContainer;
import com.gluton.glutech.registry.Registry;
import com.gluton.glutech.util.MachineItemHandler;
import com.gluton.glutech.util.NBTUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * @author Gluton
 */
public class EnergyCellTileEntity extends MachineTileEntity implements IContainer<EnergyCellContainer>{
	
	private ITextComponent customName;
	private MachineItemHandler inventory;
	
	public EnumMap<Direction, EnergyIOMode> energyIOConfig = new EnumMap<>(Direction.class);
	
	// TODO: add constants for all energy blocks --- or actually add it to IEnergyContainer/ nahhhh
	public static final int BASE_ENERGY = 0;
	public static final int CAPACITY = 100000;
	public static final int TRANSFER_IN = 1000;
	public static final int TRANSFER_OUT = 1000;
	
	public EnergyCellTileEntity() {
		super(Registry.ENERGY_CELL.getTileEntityType(), "energy_cell", BASE_ENERGY, CAPACITY, TRANSFER_IN, TRANSFER_OUT);
		
		this.inventory = this.createInventory(EnergyCellContainer.SLOTS);
		
		for (Direction d : Direction.values()) {
			this.energyIOConfig.put(d, EnergyIOMode.NONE);
		}
		
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
		setPropertyState(MachineBlock.ON, this.energy > 0);
		if (this.energy > 0) {
			ItemStack itemStack = getInventory().getStackInSlot(0);
			if (!itemStack.isEmpty()) {
				IEnergyStorage energyItem = itemStack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
				if (energyItem != null) {
					int energyToTransfer = this.extractEnergy(this.maxExtract, true);
					int energyTransfered = energyItem.receiveEnergy(energyToTransfer, false);
					if (energyTransfered > 0) {
						this.energy -= energyTransfered;
						dirty = true;
						notifyBlockUpdate(Constants.BlockFlags.BLOCK_UPDATE);
					}
				}
			}
			dirty |= transferEnergy();
		}
		return dirty;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int energyReceived = super.receiveEnergy(maxReceive, simulate);
		if (!simulate && energyReceived > 0) {
			setPropertyState(MachineBlock.ON, this.energy > 0);
		}
		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int energyExtracted = super.extractEnergy(maxExtract, simulate);
		if (!simulate && energyExtracted > 0) {
			setPropertyState(MachineBlock.ON, this.energy > 0);
		}
		return energyExtracted;
	}

	@Override
	public boolean canExtract() {
		return true;
	}
	
	public void setEnergyIOConfig(EnumMap<Direction, EnergyIOMode> energyIOConfig) {
		this.energyIOConfig = energyIOConfig;
	}
	
	/**
	 * @return cloned energyIOConfig
	 */
	public EnumMap<Direction, EnergyIOMode> getEnergyIOConfg() {
		return this.energyIOConfig.clone();
	}
	
	@Override
	public EnergyIOMode getEnergyIOModeForSide(Direction side) {
		return energyIOConfig.get(side);
	}
	
	public void nextIOMode(Direction side) {
		this.energyIOConfig.put(side, this.energyIOConfig.get(side).nextMode());
	}
	
	@Override
	public Container createMenu(final int windowId, final PlayerInventory playerInv, final PlayerEntity playerIn) {
		return new EnergyCellContainer(windowId, playerInv, this);
	}
	
	@Override
	public void loadFromNBT(CompoundNBT nbt) {
		super.loadFromNBT(nbt);
		for (Direction direction : Direction.values()) {
			this.energyIOConfig.put(direction, EnergyIOMode.values()[(int) nbt.getByte("EnergyIOConfig_" + direction.name())]);
		}
	}
	
	@Override
	public CompoundNBT saveToNBT(final CompoundNBT nbt) {
		super.saveToNBT(nbt);
		
		this.energyIOConfig.forEach((direction, ioMode) -> {
			NBTUtils.putOptionalByte(nbt, "EnergyIOConfig_" + direction.name(), (byte) ioMode.ordinal(), (byte) 0);
		});
		
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

	@Override
	public ITextComponent getCustomName() {
		return this.customName;
	}

	@Override
	public MachineItemHandler getInventory() {
		return this.inventory;
	}
}
