package com.gluton.glutech.blocks.properties;

import net.minecraft.util.IStringSerializable;

/**
 * @author Gluton
 */
public enum EnergyIOMode implements IStringSerializable {
	NONE("none"),
	INPUT("input"),
	OUTPUT("output");
	
	private final String name;
	
	private EnergyIOMode(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public String getString() {
		return this.name;
	}
	
	public EnergyIOMode nextMode() {
		return values()[(this.ordinal() + 1) % 3];
	}
}
