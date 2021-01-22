package com.gluton.glutech.items.attributes;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.nbt.CompoundNBT;

/**
 * @author Gluton
 */
public class DyanmicAttributeModifier extends AttributeModifier {

	private double amount;
	private AttributeModifier.Operation operation;
	
	/**
	 * Sets the modifier amount to 0, and we will override the value with our own, non-final amount value
	 */
	public DyanmicAttributeModifier(String nameIn, Operation operationIn) {
		super(nameIn, 0, Operation.ADDITION); // default values to not affect anything (ie adding 0)
		
		this.amount = 0;
		this.operation = operationIn;
	}

	/**
	 * Override {@link AttributeModifier#getAmount()} to return our dynamic, non-final amount value
	 * 
	 * @return the dyanamic amount
	 */
	@Override
	public double getAmount() {
		return this.amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	/**
	 * Override {@link AttributeModifier#getOperation()} to return our dynamic, non-final {@code Operation}
	 * 
	 * @return the dynamic operation
	 */
	@Override
	public Operation getOperation() {
		return this.operation;
	}
	
	public void setOperation(AttributeModifier.Operation operation) {
		this.operation = operation;
	}
	
	/**
	 * @return the string with the dynamic amount and operation
	 */
	@Override
	public String toString() {
		return "DynamicAttributeModifier{amount=" + this.amount + ", operation=" + this.operation + ", name='" + this.getName() + '\'' + ", id=" + this.getID() + '}';
	}
	
	/**
	 * Overwrite {@link AttributeModifier#write()}'s returned nbt values for
	 * "Amount" and "Operation" with our own
	 */
	@Override
	public CompoundNBT write() {
		CompoundNBT nbt = super.write();
		nbt.putDouble("Amount", this.amount);
		nbt.putInt("Operation", this.operation.getId());
		return nbt;
	}
}
