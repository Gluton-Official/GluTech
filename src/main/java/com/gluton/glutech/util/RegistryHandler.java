package com.gluton.glutech.util;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.armor.ModArmorMaterial;
import com.gluton.glutech.blocks.BlockItemBase;
import com.gluton.glutech.blocks.GlutiteOre;
import com.gluton.glutech.blocks.GlutoniumBlock;
import com.gluton.glutech.items.ItemBase;
import com.gluton.glutech.tools.ModToolMaterial;

import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Gluton
 */
public class RegistryHandler {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GluTech.MOD_ID);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GluTech.MOD_ID);
	
	public static void init() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	// Items
	public static final RegistryObject<Item> GLUTONIUM_INGOT = ITEMS.register("glutonium_ingot", ItemBase::new);
	public static final RegistryObject<Item> GLUTITE = ITEMS.register("glutite", ItemBase::new);
	
	// Tools (attack speed defaults to 4)
	public static final RegistryObject<SwordItem> GLUTONIUM_SWORD = ITEMS.register("glutonium_sword",
			() -> new SwordItem(ModToolMaterial.GLUTONIUM, 7, -2.0f, new Item.Properties().group(GluTech.TAB)));
	
	// Armor
	public static final RegistryObject<ArmorItem> GLUTONIUM_HELMET = ITEMS.register("glutonium_helmet",
			() -> new ArmorItem(ModArmorMaterial.GLUTONIUM, EquipmentSlotType.HEAD, new Item.Properties().group(GluTech.TAB)));
	public static final RegistryObject<ArmorItem> GLUTONIUM_CHESTPLATE = ITEMS.register("glutonium_chestplate",
			() -> new ArmorItem(ModArmorMaterial.GLUTONIUM, EquipmentSlotType.CHEST, new Item.Properties().group(GluTech.TAB)));
	public static final RegistryObject<ArmorItem> GLUTONIUM_LEGGINGS = ITEMS.register("glutonium_leggings",
			() -> new ArmorItem(ModArmorMaterial.GLUTONIUM, EquipmentSlotType.LEGS, new Item.Properties().group(GluTech.TAB)));
	public static final RegistryObject<ArmorItem> GLUTONIUM_BOOTS = ITEMS.register("glutonium_boots",
			() -> new ArmorItem(ModArmorMaterial.GLUTONIUM, EquipmentSlotType.FEET, new Item.Properties().group(GluTech.TAB)));
	
	// Blocks
	public static final RegistryObject<Block> GLUTONIUM_BLOCK = BLOCKS.register("glutonium_block", GlutoniumBlock::new);
	public static final RegistryObject<Block> GLUTITE_ORE = BLOCKS.register("glutite_ore", GlutiteOre::new);
	
	// Block Items
	public static final RegistryObject<Item> GLUTONIUM_BLOCK_ITEM = ITEMS.register("glutonium_block", () -> new BlockItemBase(GLUTONIUM_BLOCK.get()));
	public static final RegistryObject<Item> GLUTITE_ORE_ITEM = ITEMS.register("glutite_ore", () -> new BlockItemBase(GLUTITE_ORE.get()));
}
