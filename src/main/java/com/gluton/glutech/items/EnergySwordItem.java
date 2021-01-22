package com.gluton.glutech.items;

import java.util.List;
import java.util.function.Consumer;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.capabilities.EnergyItemProvider;
import com.gluton.glutech.registry.Registry;
import com.gluton.glutech.util.EnergyFormat;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * TODO: probably just make my own SwordItem class that doesnt extend SwordItem
 * 
 * @author Gluton
 */
public class EnergySwordItem extends SwordItem implements IEnergyItem {
	
	private int capacity;
	private final float attackSpeed;
	private Multimap<Attribute, AttributeModifier> attributeModifiers;
	
	private static final float MIN_DAMAGE = 1 + (-1); // default damage is 1
	private static final float SPEED_DEGRATION_AMOUNT = 1;
	private static final float DAMAGE_THRESHOLD = .25f;
	private static final float SPEED_THRESHOLD = .5f;

	public EnergySwordItem(IItemTier tier, int attackDamageIn, float attackSpeedIn) {
		super(tier, attackDamageIn, attackSpeedIn, new Item.Properties().group(GluTech.TAB));
		
		this.attackSpeed = attackSpeedIn;
		this.capacity = calculateCapacity(this.getTier().getMaxUses());
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		updateItemAttributes(stack);
		return new EnergyItemProvider(stack, itemStack -> updateItemAttributes(itemStack),
				itemStack -> itemStack.getOrCreateTag(), this.capacity, false, true);
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		if (slot != EquipmentSlotType.MAINHAND) {
			return super.getAttributeModifiers(slot);
		}
		
		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
	    builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 
	    		IEnergyItem.getDegradedValue(IEnergyItem.getItemEnergy(stack), this.capacity, this.getAttackDamage(),
	    		MIN_DAMAGE, 0, DAMAGE_THRESHOLD), AttributeModifier.Operation.ADDITION));
	    builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 
	    		IEnergyItem.getDegradedValue(IEnergyItem.getItemEnergy(stack), this.capacity,
	    		SPEED_DEGRATION_AMOUNT, SPEED_THRESHOLD) + (this.attackSpeed - SPEED_DEGRATION_AMOUNT), AttributeModifier.Operation.ADDITION));
	    this.attributeModifiers = builder.build();
	    return this.attributeModifiers;
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		int energy = IEnergyItem.getItemEnergy(stack);
		stack.getTag().putInt("Energy", energy - Math.min(amount * DURABILITY_TO_ENERGY, energy));
		return 0;
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		super.fillItemGroup(group, items);
		if (group == GluTech.TAB) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putInt("Energy", this.capacity);
			ItemStack itemStack = new ItemStack(Registry.GLUTONIUM_SWORD.getItem());
			initializeAttributeModifiers(itemStack);
			itemStack.setTag(nbt);
			items.add(itemStack);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(new StringTextComponent(EnergyFormat.getEnergyLabel(TextFormatting.GREEN + "Energy",
				IEnergyItem.getItemEnergy(stack), this.capacity, EnergyFormat.COMPACT, TextFormatting.GRAY, false)));
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
