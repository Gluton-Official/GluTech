package com.gluton.glutech.tileentity;

import java.util.EnumMap;
import java.util.Map.Entry;

import com.gluton.glutech.blocks.MachineBlock;
import com.gluton.glutech.blocks.properties.EnergyIOMode;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

/**
 * @author Gluton
 */
public class EnergyCellTileEntity extends MachineTileEntity<MachineRecipe> {
	
	public EnumMap<Direction, EnergyIOMode> energyIOConfig = new EnumMap<>(Direction.class);

	public int energy;
	public int capacity;
	public int maxExtract;
	public int maxReceive;
	
	public EnergyCellTileEntity() {
		// Uses maxProcessTime of 0 to ignore maximum and inventorySize of 0 since it has no inventory
		super(RegistryHandler.ENERGY_CELL.get(), "energy_cell", 0, 0);
		
		for (Direction d : Direction.values()) {
			this.energyIOConfig.put(d, EnergyIOMode.NONE);
		}
		
		this.capacity = 100000;
		this.maxExtract = 1000;
		this.maxReceive = 1000;
	}
	
	/**
	 * Is not a container, returns null
	 */
	@Override
	public Container createMenu(final int windowId, final PlayerInventory playerInv, final PlayerEntity playerIn) {
		return null;
	}

	@Override
	public void tick() {
		boolean dirty = false;
		
		if (this.world != null && !this.world.isRemote) {
			for (Direction side : Direction.values()) {
				// tile entites should only push power, not pull
				if (this.energyIOConfig.get(side) == EnergyIOMode.OUTPUT) {
					TileEntity tile = this.world.getTileEntity(this.pos.offset(side));
					// TODO: implement v
//					if (tile instanceof MachineTileEntity) {
					if (tile != null && tile instanceof CrusherTileEntity) {
						CrusherTileEntity energyTile = (CrusherTileEntity) tile;
						
						int energyToTransfer = this.extractEnergy(this.maxExtract, true);
						int energyTransfered = energyTile.receiveEnergy(energyToTransfer, false);
						if (energyTransfered > 0) {
							this.energy -= energyTransfered;
							dirty = true;
						}
					}
				}
			}
			
			this.world.setBlockState(this.getPos(), this.getBlockState().with(MachineBlock.ON, this.energy > 0));
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
			this.world.setBlockState(this.getPos(), this.getBlockState().with(MachineBlock.ON, this.energy > 0));
			this.markDirty();
			this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
		}
		return energyReceived;
	}
	
	public int receiveEnergyFromFace(Direction face, int maxReceive, boolean simulate) {
		return canReceiveFromFace(face) ? receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!canExtract()) {
			return 0;
		}
		
		int energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, maxExtract));
		if (!simulate) {
			this.energy -= energyExtracted;
			this.world.setBlockState(this.getPos(), this.getBlockState().with(MachineBlock.ON, this.energy > 0));
			this.markDirty();
			this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
		}
		return energyExtracted;
	}
	
	public int extractEnergyFromFace(Direction face, int maxExtract, boolean simulate) {
		return canExtractFromFace(face) ? extractEnergy(maxExtract, simulate) : 0;
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
		return true;
	}
	
	public boolean canExtractFromFace(Direction face) {
		return canExtract() && this.energyIOConfig.get(face) == EnergyIOMode.OUTPUT;
	}

	@Override
	public boolean canReceive() {
		return true;
	}
	
	public boolean canReceiveFromFace(Direction face) {
		return canReceive() && this.energyIOConfig.get(face) == EnergyIOMode.INPUT;
	}
	
	public void nextIOMode(Direction side) {
		this.energyIOConfig.put(side, this.energyIOConfig.get(side).nextMode());
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
		for (Direction direction : Direction.values()) {
			energyIOConfig.put(direction, EnergyIOMode.values()[(int) nbt.getByte("EnergyIOConfig_" + direction.name())]);
		}
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
		for (Entry<Direction, EnergyIOMode> entry : energyIOConfig.entrySet()) {
			nbt.putByte("EnergyIOConfig_" + entry.getKey().name(), (byte) entry.getValue().ordinal());
		}
		
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
