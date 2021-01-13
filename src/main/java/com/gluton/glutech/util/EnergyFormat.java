package com.gluton.glutech.util;

import javax.annotation.Nullable;

import net.minecraft.util.text.TextFormatting;

/**
 * @author Gluton
 */
public enum EnergyFormat {
	
	COMPACT;
	
	private static final String UNITS = "FE";
	
	/**
	 * @param fullyColored false if the color is not applied to the label name
	 */
	public static String getEnergyLabel(String labelName, int energy, int capacity, @Nullable EnergyFormat format, TextFormatting color, boolean fullyColored) {
		if (fullyColored) {
			return color + getEnergyLabel(labelName, energy, capacity, format);
		} else {
			return labelName + color + ": " + getEnergyRatio(energy, capacity, format);
		}
	}
	
	public static String getEnergyLabel(String labelName, int energy, int capacity, @Nullable EnergyFormat format) {
		return labelName + TextFormatting.RESET + ": " + getEnergyRatio(energy, capacity, format);
	}
	
	public static String getEnergyRatio(int energy, int capacity, @Nullable EnergyFormat format) {
		return String.format("%s/%s", asString(energy, format), asString(capacity, format));
	}
	
	public static String asString(int energy, @Nullable EnergyFormat format) {
		switch(format) {
			case COMPACT:
				return getCompactNumberFormat(energy) + UNITS;
			default: 
				return energy + UNITS;
		}
	}

	public static String getCompactNumberFormat(float number) {
		if (number >= 1e15f) {
			return roundTo3Digits(number / 1e15f) + "Q";
		} else if (number >= 1e12f) {
			return roundTo3Digits(number / 1e12f) + "T";
		} else if (number >= 1e9f) {
			return roundTo3Digits(number / 1e9f) + "B";
		} else if (number >= 1e6f) {
			return roundTo3Digits(number / 1e6f) + "M";
		} else if (number >= 1e3f) {
			return roundTo3Digits(number / 1e3f) + "K";
		} else {
			return roundTo3Digits(number) + "";
		}
	}
	
	public static String roundTo3Digits(float number) {
		if (number >= 100) {
			return String.format("%.0f", number);
		} else if (number >= 10) {
			return String.format("%.1f", number);
		} else {
			return String.format("%.2f", number);
		}
	}
}
