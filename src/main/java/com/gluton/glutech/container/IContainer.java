package com.gluton.glutech.container;

import javax.annotation.Nullable;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.util.MachineItemHandler;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

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
		if (customName != null) {
			nbt.putString("CustomName", ITextComponent.Serializer.toJson(customName));
		}	
		return nbt;
	}
	
	default NonNullList<ItemStack> readInventoryFromNBT(CompoundNBT nbt, int slots) {
		NonNullList<ItemStack> inventory = NonNullList.withSize(slots, ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(nbt, inventory);
		return inventory;
	}
	
	default CompoundNBT writeInventoryToNBT(CompoundNBT nbt) {
		MachineItemHandler inventoryIn = getInventory();
		if (inventoryIn != null && !inventoryIn.isEmpty()) {
			NonNullList<ItemStack> items = NonNullList.create();
			for (int i = 0; i < inventoryIn.getSlots(); i++) {
				items.add(inventoryIn.getStackInSlot(i));
			}
			nbt = ItemStackHelper.saveAllItems(nbt, items);
		}
		return nbt;
	}
	
	default MachineItemHandler createInventory(int size) {
		return new MachineItemHandler(size);
	}
	
	default void spillInventory(World worldIn, BlockPos pos) {
		getInventory().toNonNullList().forEach(item -> {
			worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), item));
		});
	}
	
	String getName();
	
	void setCustomName(ITextComponent customName);
	
	@Nullable
	ITextComponent getCustomName();
	
	MachineItemHandler getInventory();
}
