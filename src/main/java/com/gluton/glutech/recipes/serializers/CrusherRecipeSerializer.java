package com.gluton.glutech.recipes.serializers;

import com.gluton.glutech.recipes.CrusherRecipe;
import com.google.gson.JsonObject;

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
public class CrusherRecipeSerializer extends MachineRecipeSerializer<CrusherRecipe> {

	@Override
	public CrusherRecipe read(ResourceLocation recipeId, JsonObject json) {
		NonNullList<Ingredient> ingredients = NonNullList.from(null,
				Ingredient.deserialize(JSONUtils.getJsonObject(json, "input")));
		
		ItemStack output = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "output"), true);
		
		return new CrusherRecipe(recipeId, ingredients, output);
	}
	
	@Override
	public CrusherRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		NonNullList<Ingredient> ingredients = NonNullList.from(null,Ingredient.read(buffer));
		
		ItemStack output = buffer.readItemStack();
		
		return new CrusherRecipe(recipeId, ingredients, output);
	}
	
	@Override
	public void write(PacketBuffer buffer, CrusherRecipe recipe) {
		for (Ingredient ingredient : recipe.getIngredients()) {
			ingredient.write(buffer);
		}
		
		buffer.writeItemStack(recipe.getRecipeOutput(), false);
	}
}
