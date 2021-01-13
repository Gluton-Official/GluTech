package com.gluton.glutech.tileentity;

import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;

/**
 * @author Gluton
 */
public abstract class CapabilityCallable<T> {

	public T storage;
	
	public CapabilityCallable(T storage) {
		this.storage = storage;
	}
	
	public abstract LazyOptional<T> side(Direction side);
}
