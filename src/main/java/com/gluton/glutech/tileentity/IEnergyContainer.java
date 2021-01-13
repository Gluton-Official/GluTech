package com.gluton.glutech.tileentity;

import com.gluton.glutech.blocks.properties.EnergyIOMode;

import net.minecraft.util.Direction;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * @author Gluton
 */
public interface IEnergyContainer extends IEnergyStorage {
	
	default int receiveEnergyFromFace(Direction face, int maxReceive, boolean simulate) {
		return canReceiveFromFace(face) ? receiveEnergy(maxReceive, simulate) : 0;
	}
	
	default int extractEnergyFromFace(Direction face, int maxExtract, boolean simulate) {
		return canExtractFromFace(face) ? extractEnergy(maxExtract, simulate) : 0;
	}
	
	default boolean canReceiveFromFace(Direction face) {
		return canReceive() && getEnergyIOModeForSide(face) == EnergyIOMode.INPUT;
	}
	
	default boolean canExtractFromFace(Direction face) {
		return canExtract() && getEnergyIOModeForSide(face) == EnergyIOMode.OUTPUT;
	}

	/**
	 * Defaults to {@code false}
	 */
	@Override
	default boolean canExtract() {
		return false;
	}
	
	/**
	 * Defaults to {@code true}
	 */
	@Override
	default boolean canReceive() {
		return true;
	}
	
	void setEnergyStored(int energy);
	
	void setMaxEnergyStored(int capacity);
	
	/**
	 * Defaults to {@link EnergyIOMode#INPUT}
	 */
	default EnergyIOMode getEnergyIOModeForSide(Direction side) {
		return EnergyIOMode.INPUT;
	}
}
