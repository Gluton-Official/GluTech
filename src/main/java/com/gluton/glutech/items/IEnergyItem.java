package com.gluton.glutech.items;

import net.minecraft.item.ItemStack;

/**
 * @author Gluton
 */
public interface IEnergyItem {

	public static final int GREEN = 0x0000ff00;
	public static final int BLUE = 0x000000ff;
	
	public static final int DURABILITY_TO_ENERGY = 1000;
	
	public static int getItemEnergy(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains("Energy")) {
			return stack.getTag().getInt("Energy");
		}
		return 0;
	}
	
	public static double getDegradedValue(int energy, int capacity, float range) {
		return range * (energy / capacity);
	}
	
	public static double getDegradedValue(int energy, int capacity, float range, float upperThreshold) {
		return Math.min(range, range * (energy / (capacity * upperThreshold)));
	}
	
	public static double getDegradedValue(int energy, int capacity, float range, float lowerThreshold, float upperThreshold) {
		return Math.max(0, Math.min(range, range * (energy / (capacity * (upperThreshold - lowerThreshold))) - range * (lowerThreshold / (upperThreshold - lowerThreshold))));
	}
	
	public static double getDegradedValue(int energy, int capacity, float range, float minValue, float lowerThreshold, float upperThreshold) {
		return Math.max(minValue, Math.min(range, ((range - minValue) / range) * (range * (energy / (capacity * (upperThreshold - lowerThreshold))) - range * (lowerThreshold / (upperThreshold - lowerThreshold)))));
	}
	
	default int calculateCapacity(int durability) {
		return (int) (durability * DURABILITY_TO_ENERGY * .4);
	}
	
	default void initializeAttributeModifiers(ItemStack stack) {
	}
	
	default void updateItemAttributes(ItemStack stack) {
	}
}
