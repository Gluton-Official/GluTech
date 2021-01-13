package com.gluton.glutech.armor;

import java.util.function.Supplier;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.registry.Registry;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

/**
 * @author Gluton
 */
public enum ModArmorMaterial implements IArmorMaterial {
	GLUTONIUM(GluTech.MOD_ID + ":glutonium", 41, new int[] {4, 7, 9, 4}, 15, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3.0f,
			() -> { return Ingredient.fromItems(Registry.GLUTONIUM_INGOT.getItem()); }, 0.1f);

	private static final int[] BASE_DURBILITY = new int[] {11, 16, 15, 13};
	private final String name;
	private final int durabilityMultiplier;
	private final int[] damageReductionAmount;
	private final int enchantability;
	private final SoundEvent soundEvent;
	private final float toughness;
	private final Supplier<Ingredient> repairMaterial;
	private final float knockbackResistance;
	
	ModArmorMaterial(String name, int durabilityMultiplier, int[] damageReductionAmount, int enchantability, 
				SoundEvent soundEvent, float toughness, Supplier<Ingredient> repairMaterial, float knockbackResistance) {
		this.name = name;
		this.durabilityMultiplier = durabilityMultiplier;
		this.damageReductionAmount = damageReductionAmount;
		this.enchantability = enchantability;
		this.soundEvent = soundEvent;
		this.toughness = toughness;
		this.repairMaterial = repairMaterial;
		this.knockbackResistance = knockbackResistance;
	}
	
	@Override
	public int getDurability(EquipmentSlotType slotIn) {
		return BASE_DURBILITY[slotIn.getIndex()] * this.durabilityMultiplier;
	}

	@Override
	public int getDamageReductionAmount(EquipmentSlotType slotIn) {
		return this.damageReductionAmount[slotIn.getIndex()];
	}

	@Override
	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public SoundEvent getSoundEvent() {
		return this.soundEvent;
	}

	@Override
	public Ingredient getRepairMaterial() {
		return this.repairMaterial.get();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public float getToughness() {
		return this.toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}
}
