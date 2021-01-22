package com.gluton.glutech.items;

import java.util.List;
import java.util.function.Consumer;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.capabilities.EnergyItemProvider;
import com.gluton.glutech.registry.Registry;
import com.gluton.glutech.util.EnergyFormat;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * @author Gluton
 */
public class EnergyArmorItem extends ArmorItem implements IEnergyItem {
	
	private int capacity;
	
	private static final float ARMOR_THRESHOLD = .25f;
	private static final float TOUGHNESS_THRESHOLD = .50f;
	private static final float KNOCKBACK_THRESHOLD = .75f;

	public EnergyArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot) {
		super(materialIn, slot, new Item.Properties().group(GluTech.TAB));
		
		this.capacity = calculateCapacity(materialIn.getDurability(slot));
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		initializeAttributeModifiers(stack);
		updateItemAttributes(stack);
		return new EnergyItemProvider(stack, itemStack -> updateItemAttributes(itemStack),
				itemStack -> itemStack.getOrCreateTag(), this.capacity, false, true);
	}
	
	@Override
	public void initializeAttributeModifiers(ItemStack stack) {
		if (!stack.getAttributeModifiers(this.slot).get(Attributes.ARMOR).stream().anyMatch(attribute -> attribute.getName().equals("Dynamic Armor"))) {
			stack.addAttributeModifier(Attributes.ARMOR, new AttributeModifier("Dynamic Armor", this.getDamageReduceAmount(), Operation.ADDITION), this.slot);
		}
		if (!stack.getAttributeModifiers(this.slot).get(Attributes.ARMOR_TOUGHNESS).stream().anyMatch(attribute -> attribute.getName().equals("Dynamic Armor Toughness"))) {
			stack.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier("Dynamic Armor Toughness", this.getDamageReduceAmount(), Operation.ADDITION), this.slot);
		}
		if (!stack.getAttributeModifiers(this.slot).get(Attributes.KNOCKBACK_RESISTANCE).stream().anyMatch(attribute -> attribute.getName().equals("Dynamic Knockback Resistance"))) {
			stack.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier("Dynamic Knockback Resistance", this.getDamageReduceAmount(), Operation.ADDITION), this.slot);
		}
	}
	
//	@Override
//	public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
//		super.onArmorTick(stack, world, player);
//		updateItemAttributes(stack);
//	}
	
	@Override
	public void updateItemAttributes(ItemStack stack) {
		INBT modifiers = stack.getTag().get("AttributeModifiers");
		if (modifiers != null && modifiers.getType() == ListNBT.TYPE) {
			ListNBT modifierList = (ListNBT) modifiers;
			if (!modifierList.isEmpty()) {
				modifierList.forEach(element -> {
					CompoundNBT modifier = null;
					if (element instanceof CompoundNBT) {
						modifier = (CompoundNBT) element;
					}
					if (modifier != null) {
						switch (modifier.getString("Name")) {
							case "Dynamic Armor":
								modifier.putDouble("Amount", IEnergyItem.getDegradedValue(IEnergyItem.getItemEnergy(stack),
										this.capacity, this.getDamageReduceAmount(), ARMOR_THRESHOLD));
								break;
							case "Dynamic Armor Toughness":
								modifier.putDouble("Amount", IEnergyItem.getDegradedValue(IEnergyItem.getItemEnergy(stack),
										this.capacity, this.getToughness(), ARMOR_THRESHOLD, TOUGHNESS_THRESHOLD));
								break;
							case "Dynamic Knockback Resistance":
								modifier.putDouble("Amount", IEnergyItem.getDegradedValue(IEnergyItem.getItemEnergy(stack),
										this.capacity, this.knockbackResistance, TOUGHNESS_THRESHOLD, KNOCKBACK_THRESHOLD));
								break;
							default:
								return;
						}
					}
				});
			}
		}
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		int energy = IEnergyItem.getItemEnergy(stack);
		stack.getTag().putInt("Energy", energy - Math.min(amount * DURABILITY_TO_ENERGY, energy));
		updateItemAttributes(stack);
		return 0;
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		super.fillItemGroup(group, items);
		if (group == GluTech.TAB) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putInt("Energy", this.capacity);
			ItemStack itemStack = null;
			switch (this.getEquipmentSlot()) {
				case HEAD:
					itemStack = new ItemStack(Registry.GLUTONIUM_HELMET.getItem()); break;
				case CHEST:
					itemStack = new ItemStack(Registry.GLUTONIUM_CHESTPLATE.getItem()); break;
				case LEGS:
					itemStack = new ItemStack(Registry.GLUTONIUM_LEGGINGS.getItem()); break;
				case FEET:
					itemStack = new ItemStack(Registry.GLUTONIUM_BOOTS.getItem()); break;
				default:
					return;
			}
			itemStack.setTag(nbt);
			items.add(itemStack);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		int energy = IEnergyItem.getItemEnergy(stack);
		tooltip.add(new StringTextComponent(EnergyFormat.getEnergyLabel(TextFormatting.GREEN + "Energy",
				energy, this.capacity, EnergyFormat.COMPACT, TextFormatting.GRAY, false)));
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1 - (IEnergyItem.getItemEnergy(stack) / (double) capacity);
	}
	
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return BLUE;
	}
	
	@Override
	public int getDamage(ItemStack stack) {
		return 0;
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}
}
