package com.gluton.glutech.container;

import javax.annotation.Nullable;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.util.MachineItemHandler;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.ItemStackHandler;

/**
 * @author Gluton
 */
public interface IContainer<T extends MachineContainer> extends INamedContainerProvider {

	default ITextComponent getDisplayName() {
		return getCustomName() != null ? getCustomName() : getUnlocalizedName();
	}
	
	default ITextComponent getUnlocalizedName() {
		return new TranslationTextComponent("container." + GluTech.MOD_ID + "." + getName());
	}
	
	default void readCustomNameFromNBT(CompoundNBT nbt) {
		setCustomName(ITextComponent.Serializer.getComponentFromJson(nbt.getString("CustomName")));
	}
	
	default CompoundNBT writeCustomNameToNBT(CompoundNBT nbt) {
		ITextComponent customName = getCustomName();
		if (customName == null) {
			return nbt;
		}
		
		nbt.putString("CustomName", ITextComponent.Serializer.toJson(customName));
		return nbt;
	}
	
	default NonNullList<ItemStack> readInventoryFromNBT(CompoundNBT nbt, int slots) {
		NonNullList<ItemStack> inventory = NonNullList.withSize(slots, ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(nbt, inventory);
		return inventory;
	}
	
	default CompoundNBT writeInventoryToNBT(CompoundNBT nbt) {
		ItemStackHandler inventoryIn = getInventory();
		NonNullList<ItemStack> inventory = NonNullList.create();
		if (inventoryIn != null) {
			for (int i = 0; i < inventoryIn.getSlots(); i++) {
				inventory.add(inventoryIn.getStackInSlot(i));
			}
		}
		return ItemStackHelper.saveAllItems(nbt, inventory);
	}
	
	default MachineItemHandler createInventory(int size) {
		return new MachineItemHandler(size);
	}
	
	String getName();
	
	void setCustomName(ITextComponent customName);
	
	@Nullable
	ITextComponent getCustomName();
	
	ItemStackHandler getInventory();
}
