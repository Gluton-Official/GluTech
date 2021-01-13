package com.gluton.glutech.recipes;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.registry.Registry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

/**
 * @author Gluton
 */
public class SintererRecipe extends Recipe {
	
	public static final ResourceLocation RECIPE_ID = new ResourceLocation(GluTech.MOD_ID, "sinterer");

	public SintererRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack output) {
		super(id, ingredients, output, Registry.SINTERER);
	}
}
