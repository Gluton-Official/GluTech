package com.gluton.glutech.recipes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

/**
 * @author Gluton
 */
public interface IProcessor<R extends Recipe> {

	@SuppressWarnings("unchecked")
	@Nullable
	default R getRecipe(World worldIn, IItemHandlerModifiable inventoryIn, ItemStack ...stacksIn) {
		for (ItemStack stack : stacksIn) {
			if (stack == null) {
				return null;
			}
		}
		
		if (getCachedRecipe() != null && getCachedRecipe().checkIngredients(stacksIn)) {
			return getCachedRecipe().getRecipe();
		}
		
		Set<IRecipe<?>> recipes = findRecipesByType(getRecipeType(), worldIn);
		for (IRecipe<?> irecipe : recipes) {
			R recipe = (R) irecipe;
			if (recipe.matches(new RecipeWrapper(inventoryIn), worldIn)) {
				setCachedRecipe(new CachedRecipe<R>(recipe, stacksIn));
				return recipe;
			}
		}
		
		return null;
	}
	
	public static Set<IRecipe<?>> findRecipesByType(IRecipeType<?> typeIn, World world) {
		return world != null ? world.getRecipeManager().getRecipes().stream()
				.filter(recipe -> recipe.getType() == typeIn).collect(Collectors.toSet()) : Collections.emptySet();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static Set<IRecipe<?>> findRecipesByType(IRecipeType<?> typeIn) {
		ClientWorld world = Minecraft.getInstance().world;
		return world != null ? world.getRecipeManager().getRecipes().stream()
				.filter(recipe -> recipe.getType().equals(typeIn)).collect(Collectors.toSet()) : Collections.emptySet();
	}
	
	public static Set<ItemStack> getAllRecipeInputs(IRecipeType<?> typeIn, World worldIn) {
		Set<ItemStack> inputs = new HashSet<ItemStack>();
		Set<IRecipe<?>> recipes = findRecipesByType(typeIn, worldIn);
		for (IRecipe<?> recipe : recipes) {
			NonNullList<Ingredient> ingredients = recipe.getIngredients();
			ingredients.forEach(ingredient -> {
				for (ItemStack stack : ingredient.getMatchingStacks()) {
					inputs.add(stack);
				}
			});
		}
		return inputs;
	}
	
	public static boolean outputAvailable(Recipe recipe, ItemStack outputStack) {
		if (outputStack.isEmpty()) {
			return true;
		}
		return Container.areItemsAndTagsEqual(recipe.getRecipeOutput(), outputStack)
				&& outputStack.getCount() < outputStack.getMaxStackSize();
	}
	
	void halt();
	
	void setCurrentProcessTime(int currentProcessTime);
	
	int getCurrentProcessTime();
	
	void setMaxProcessTime(int maxProcessTime);
	
	int getMaxProcessTime();
	
	void setCachedRecipe(CachedRecipe<R> cachedRecipe);
	
	CachedRecipe<R> getCachedRecipe();
	
	IRecipeType<Recipe> getRecipeType();
}
