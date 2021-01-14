package com.gluton.glutech.recipes.serializers;

import com.gluton.glutech.recipes.SintererRecipe;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

/**
 * @author Gluton
 */
public class SintererRecipeSerializer extends MachineRecipeSerializer<SintererRecipe> {
	
	@Override
	public ResourceLocation getRecipeId() {
		return SintererRecipe.RECIPE_ID;
	}

	@Override
	public SintererRecipe read(ResourceLocation recipeId, JsonObject json) {
		NonNullList<Ingredient> ingredients = readIngredients(json.getAsJsonArray("inputs"));
		if (ingredients.size() != 2) {
			throw new JsonParseException("Sinterer recipe " + recipeId.toString() + " has incorrect number of inputs (required 2)");
		}
		
		ItemStack output = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "output"), true);
		
		return new SintererRecipe(recipeId, ingredients, output);
	}
	
	@Override
	public SintererRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		int numInputs = buffer.readInt();
		NonNullList<Ingredient> ingredients = readIngredients(numInputs, buffer);
		if (ingredients.size() != 2) {
			throw new JsonParseException("Sinterer recipe" + recipeId.toString() + " has incorrect number of inputs (required 2)");
		}
		
		ItemStack output = buffer.readItemStack();
		
		return new SintererRecipe(recipeId, ingredients, output);
	}
}
