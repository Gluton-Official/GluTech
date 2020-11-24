package com.gluton.glutech.recipes.serializers;

import com.gluton.glutech.recipes.MachineRecipe;
import com.google.gson.JsonArray;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * @author Gluton
 *
 */
public abstract class MachineRecipeSerializer<R extends MachineRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> 
		implements IRecipeSerializer<R> {
	
	protected NonNullList<Ingredient> readIngredients(JsonArray jsonArray) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (int i = 0; i < jsonArray.size(); i++) {
			ingredients.add(Ingredient.deserialize(jsonArray.get(i)));
		}
		return ingredients;
	}
	
	protected NonNullList<Ingredient> readIngredients(int size, PacketBuffer buffer) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (int i = 0; i < size; i++) {
			ingredients.add(Ingredient.read(buffer));
		}
		return ingredients;
	}
}