package com.gluton.glutech.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.items.wrapper.RecipeWrapper;

/**
 * @author Gluton
 */
public class CachedRecipe<R extends IRecipe<RecipeWrapper>> {

	private R recipe;
	private ItemStack[] ingredients;
	
	public CachedRecipe(R recipe, ItemStack ...stacks) {
		this.recipe = recipe;
		this.ingredients = stacks;
	}
	
	public boolean checkIngredients(ItemStack ...stacks) {
		return this.ingredients == stacks;
	}
	
	public R getRecipe() {
		return this.recipe;
	}
}
