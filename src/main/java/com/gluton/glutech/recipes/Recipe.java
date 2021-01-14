package com.gluton.glutech.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.gluton.glutech.registry.RegistryHandler.RegisteredRecipeSerializer;

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
public abstract class Recipe implements IRecipe<RecipeWrapper> {
	
	private final ResourceLocation id;
	private NonNullList<Ingredient> ingredients;
	private final ItemStack output;
	private final RegisteredRecipeSerializer<?, ?, ?, ?, ?> serializer;
	
	public Recipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack output, RegisteredRecipeSerializer<?, ?, ?, ?, ?> serializer) {
		this.id = id;
		this.ingredients = ingredients;
		this.output = output;
		this.serializer = serializer;
	}
	
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
	
	@Nonnull
	@Override
	public IRecipeType<?> getType() {
		return serializer.getRecipeType();
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return serializer.getRecipeSerializer();
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return this.ingredients;
	}
}
