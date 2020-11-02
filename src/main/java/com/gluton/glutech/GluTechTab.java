package com.gluton.glutech;

import com.gluton.glutech.util.RegistryHandler;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

/**
 * @author Gluton
 */
public class GluTechTab extends ItemGroup {

	public GluTechTab() {
		super("glutech");
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(RegistryHandler.GLUTONIUM_INGOT.get());
	}

}
