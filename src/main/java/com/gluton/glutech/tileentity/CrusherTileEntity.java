package com.gluton.glutech.tileentity;

import javax.annotation.Nullable;

import com.gluton.glutech.blocks.MachineBlock;
import com.gluton.glutech.capabilities.CapabilityCallable;
import com.gluton.glutech.container.CrusherContainer;
import com.gluton.glutech.container.IContainer;
import com.gluton.glutech.recipes.CachedRecipe;
import com.gluton.glutech.recipes.CrusherRecipe;
import com.gluton.glutech.recipes.IProcessor;
import com.gluton.glutech.recipes.Recipe;
import com.gluton.glutech.registry.Registry;
import com.gluton.glutech.util.MachineItemHandler;
import com.gluton.glutech.util.NBTUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * @author Gluton
 */
public class CrusherTileEntity extends MachineTileEntity implements IProcessor<CrusherRecipe>, IContainer<CrusherContainer> {
	
	private int usageRate;
	private int currentProcessTime;
	private int maxProcessTime;
	private ITextComponent customName;
	private MachineItemHandler inventory;
	
	private CachedRecipe<CrusherRecipe> cachedRecipe = null;
	
	public static final int BASE_ENERGY = 0;
	public static final int CAPACITY = 10000;
	public static final int TRANSFER_IN = 1000;
	public static final int TRANSFER_OUT = 0;
	
	public CrusherTileEntity() {
		super(Registry.CRUSHER.getTileEntityType(), "crusher", BASE_ENERGY, CAPACITY, TRANSFER_IN, TRANSFER_OUT);
		
		this.usageRate = 20;
		this.maxProcessTime = 100;
		
		this.inventory = this.createInventory(CrusherContainer.SLOTS);
		
		this.capabilities.put(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, new CapabilityCallable<IItemHandler>(this.inventory) {
			@Override
			public LazyOptional<IItemHandler> side(Direction side) {
				return LazyOptional.of(() -> this.storage); 
			}
		});
	}
	
	@Override
	public boolean machineTick() {
		if (!isRedstonePowered() && hasEnergyAvailable()) {
			CrusherRecipe recipe = getRecipe(this.world, this.inventory, this.inventory.getStackInSlot(0));
			if (recipe != null && IProcessor.outputAvailable(recipe, this.inventory.getStackInSlot(1))) {
				if (this.currentProcessTime < this.maxProcessTime) {
					setPropertyState(MachineBlock.ON, true);
					this.currentProcessTime++;
					this.energy -= this.usageRate;
				} else {
					halt();
					ItemStack output = this.getRecipe(this.world, this.inventory, this.inventory.getStackInSlot(0)).getRecipeOutput();
					this.inventory.insertItem(1, output.copy(), false);
					this.inventory.decrStackSize(0, 1);
				}
				return true;
			}
		}
		
		if (!isRedstonePowered()) {
			if (this.currentProcessTime != 0) {
				halt();
			}
		} else {
			setPropertyState(MachineBlock.ON, false);
		}
		
			
		return false;
	}
	
	// TODO: make lambda function supplier??
//	@Override
	public boolean hasEnergyAvailable() {
		return this.energy >= this.usageRate;
	}
	
	@Override
	public void halt() {
		this.currentProcessTime = 0;
		setPropertyState(MachineBlock.ON, false);
	}
	
	@Override
	public CrusherContainer createMenu(final int windowId, final PlayerInventory playerInv, final PlayerEntity playerIn) {
		return new CrusherContainer(windowId, playerInv, this);
	}
	
	@Override
	public void loadFromNBT(CompoundNBT nbt) {
		super.loadFromNBT(nbt);
		
		readCustomNameFromNBT(nbt);
		
		this.inventory.setNonNullList(readInventoryFromNBT(nbt, this.inventory.getSlots()));
		
		this.currentProcessTime = nbt.getInt("CurrentProcessTime");
	}
	
	@Override
	public CompoundNBT saveToNBT(CompoundNBT nbt) {
		nbt = super.saveToNBT(nbt);
		
		nbt = writeCustomNameToNBT(nbt);
		nbt = writeInventoryToNBT(nbt);
		
		NBTUtils.putOptionalInt(nbt, "CurrentProcessTime", this.currentProcessTime, 0);
		
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
	
	@Override
	public void setCurrentProcessTime(int currentProcessTime) {
		this.currentProcessTime = currentProcessTime;
	}
	
	@Override
	public int getCurrentProcessTime() {
		return this.currentProcessTime;
	}
	
	@Override
	public void setMaxProcessTime(int maxProcessTime) {
		this.maxProcessTime = maxProcessTime;
	}
	
	@Override
	public int getMaxProcessTime() {
		return this.maxProcessTime;
	}
	
	@Override
	public void setCachedRecipe(CachedRecipe<CrusherRecipe> recipe) {
		this.cachedRecipe = recipe;
	}
	
	@Override
	public CachedRecipe<CrusherRecipe> getCachedRecipe() {
		return this.cachedRecipe;
	}

	@Override
	public IRecipeType<Recipe> getRecipeType() {
		return Registry.CRUSHER.getRecipeType();
	}
}
