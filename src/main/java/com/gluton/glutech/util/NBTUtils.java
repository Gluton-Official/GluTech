package com.gluton.glutech.util;

import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;

/**
 * @author Gluton
 */
public class NBTUtils {
	
	public static void putOptional(CompoundNBT nbt, String key, INBT value, INBT defaultValue) {
		if (!value.toString().equals(defaultValue.toString())) {
			nbt.put(key, value);
		}
	}

	public static void putOptionalBoolean(CompoundNBT nbt, String key, boolean value, boolean defaultValue) {
		if (value != defaultValue) {
			nbt.putBoolean(key, value);
		}
	}
	
	public static void putOptionalByte(CompoundNBT nbt, String key, byte value, byte defaultValue) {
		if (value != defaultValue) {
			nbt.putByte(key, value);
		}
	}
	
	public static void putOptionalByteArray(CompoundNBT nbt, String key, byte[] value, byte[] defaultValue) {
		if (value != defaultValue) {
			nbt.putByteArray(key, value);
		}
	}
	
	public static void putOptionalDouble(CompoundNBT nbt, String key, double value, double defaultValue) {
		if (value != defaultValue) {
			nbt.putDouble(key, value);
		}
	}
	
	public static void putOptionalFloat(CompoundNBT nbt, String key, float value, float defaultValue) {
		if (value != defaultValue) {
			nbt.putFloat(key, value);
		}
	}
	
	public static void putOptionalInt(CompoundNBT nbt, String key, int value, int defaultValue) {
		if (value != defaultValue) {
			nbt.putInt(key, value);
		}
	}
	
	public static void putOptionalIntArray(CompoundNBT nbt, String key, int[] value, int[] defaultValue) {
		if (value != defaultValue) {
			nbt.putIntArray(key, value);
		}
	}
	
	public static void putOptionalIntArray(CompoundNBT nbt, String key, List<Integer> value, List<Integer> defaultValue) {
		if (!value.equals(defaultValue)) {
			nbt.putIntArray(key, value);
		}
	}
	
	public static void putOptionalLong(CompoundNBT nbt, String key, long value, long defaultValue) {
		if (value != defaultValue) {
			nbt.putLong(key, value);
		}
	}
	
	public static void putOptionalLongArray(CompoundNBT nbt, String key, long[] value, long[] defaultValue) {
		if (value != defaultValue) {
			nbt.putLongArray(key, value);
		}
	}
	
	public static void putOptionalLongArray(CompoundNBT nbt, String key, List<Long> value, List<Long> defaultValue) {
		if (!value.equals(defaultValue)) {
			nbt.putLongArray(key, value);
		}
	}
	
	public static void putOptionalShort(CompoundNBT nbt, String key, short value, short defaultValue) {
		if (value != defaultValue) {
			nbt.putShort(key, value);
		}
	}
	
	public static void putOptionalString(CompoundNBT nbt, String key, String value, String defaultValue) {
		if (!value.equals(defaultValue)) {
			nbt.putString(key, value);
		}
	}
	
	public static void putOptionalUniqueId(CompoundNBT nbt, String key, UUID value, UUID defaultValue) {
		if (!value.equals(defaultValue)) {
			nbt.putUniqueId(key, value);
		}
	}
}
