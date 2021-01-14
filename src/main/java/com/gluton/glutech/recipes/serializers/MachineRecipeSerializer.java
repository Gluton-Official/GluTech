package com.gluton.glutech.recipes.serializers;

import com.gluton.glutech.recipes.Recipe;
import com.google.gson.JsonArray;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * @author Gluton
 *
 */
public abstract class MachineRecipeSerializer<R extends Recipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> 
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
	
	@Override
	public void write(PacketBuffer buffer, R recipe) {
		for (Ingredient ingredient : recipe.getIngredients()) {
			ingredient.write(buffer);
		}
		
		buffer.writeItemStack(recipe.getRecipeOutput(), false);
	}
	
	public abstract ResourceLocation getRecipeId();
}