package com.gluton.glutech.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.RecipeWrapper;

/**
 * @author Gluton
 */
public abstract class MachineRecipe implements IRecipe<RecipeWrapper> {
	
	private final ResourceLocation id;
	private NonNullList<Ingredient> ingredients;
	private final ItemStack output;
	
	public MachineRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack output) {
		this.id = id;
		this.ingredients = ingredients;
		this.output = output;
	}
	
	@Nonnull
	@Override
	public abstract IRecipeType<?> getType();
	
	@Override
	public boolean canFit(int width, int height) {
		return false;
	}
	
	@Override
	public boolean matches(RecipeWrapper inv, World worldIn) {
		List<Ingredient> inputsNeeded = new ArrayList<Ingredient>(this.ingredients);
		Collections.copy(inputsNeeded, this.ingredients);
		for (int slot = 0; slot < this.ingredients.size(); slot++) {
			boolean flag = false;
			for (int i = 0; i < inputsNeeded.size(); i++) {
				boolean test = inputsNeeded.get(i).test(inv.getStackInSlot(slot));
				if (test) {
					flag = true;
					inputsNeeded.remove(i--);
				}
			}
			if (!flag) {
				return false;
			}
		}
		return inputsNeeded.isEmpty();
	}

	@Override
	public ItemStack getCraftingResult(RecipeWrapper inv) {
		return this.output;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.output;
	}
	
	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public abstract IRecipeSerializer<?> getSerializer();

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return this.ingredients;
	}
}
