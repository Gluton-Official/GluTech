package com.gluton.glutech.tileentity;

import com.gluton.glutech.blocks.MachineBlock;
import com.gluton.glutech.container.SintererContainer;
import com.gluton.glutech.recipes.MachineRecipe;
import com.gluton.glutech.recipes.SintererRecipe;
import com.gluton.glutech.util.RegistryHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.common.util.Constants;

/**
 * @author Gluton
 */
public class SintererTileEntity extends MachineTileEntity<SintererRecipe> {

	public SintererTileEntity() {
		super(RegistryHandler.SINTERER.get(), "sinterer", SintererContainer.SLOTS, 100);
	}
	
	@Override
	public Container createMenu(final int windowId, final PlayerInventory playerInv, final PlayerEntity playerIn) {
		return new SintererContainer(windowId, playerInv, this);
	}

	@Override
	public void tick() {
		boolean dirty = false;
		
		if (this.world != null && !this.world.isRemote) {
			if (this.world.isBlockPowered(this.getPos())) {
				SintererRecipe recipe = this.getRecipe(this.inventory.getStackInSlot(0), this.inventory.getStackInSlot(1));
				if (recipe != null && outputAvailable(recipe, this.inventory.getStackInSlot(2))) {
					if (this.currentProcessTime < this.maxProcessTime) {
						this.world.setBlockState(this.getPos(), this.getBlockState().with(MachineBlock.ON, true));
						this.currentProcessTime++;
						dirty = true;
					} else {
						this.world.setBlockState(this.getPos(), this.getBlockState().with(MachineBlock.ON, false));
						this.currentProcessTime = 0;
						ItemStack output = this.getRecipe(this.inventory.getStackInSlot(0), this.inventory.getStackInSlot(1)).getRecipeOutput();
						this.inventory.insertItem(2, output.copy(), false);
						this.inventory.decrStackSize(0, 1);
						this.inventory.decrStackSize(1, 1);
						dirty = true;
					}
				} else if (this.currentProcessTime != 0){
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
	protected IRecipeType<MachineRecipe> getRecipeType() {
		return RegistryHandler.SINTERER_RECIPE_TYPE;
	}
}
