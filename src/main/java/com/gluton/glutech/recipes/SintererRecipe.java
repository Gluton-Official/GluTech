package com.gluton.glutech.recipes;

import javax.annotation.Nonnull;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.util.RegistryHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

/**
 * @author Gluton
 */
public class SintererRecipe extends MachineRecipe {
	
	public static final ResourceLocation RECIPE_ID = new ResourceLocation(GluTech.MOD_ID, "sinterer");

	public SintererRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack output) {
		super(id, ingredients, output);
	}
	
	@Nonnull
	@Override
	public IRecipeType<?> getType() {
		return Registry.RECIPE_TYPE.getOptional(RECIPE_ID).get();
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return RegistryHandler.SINTERER_SERIALIZER.get();
	}
}
