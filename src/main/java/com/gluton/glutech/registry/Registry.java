package com.gluton.glutech.registry;

import java.util.function.Supplier;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.armor.ModArmorMaterial;
import com.gluton.glutech.blocks.CrusherBlock;
import com.gluton.glutech.blocks.EnergyCellBlock;
import com.gluton.glutech.blocks.FurnaceGeneratorBlock;
import com.gluton.glutech.blocks.GlutiteOreBlock;
import com.gluton.glutech.blocks.GlutoniumBlock;
import com.gluton.glutech.blocks.SintererBlock;
import com.gluton.glutech.container.CrusherContainer;
import com.gluton.glutech.container.FurnaceGeneratorContainer;
import com.gluton.glutech.container.SintererContainer;
import com.gluton.glutech.items.EnergyCellItem;
import com.gluton.glutech.recipes.CrusherRecipe;
import com.gluton.glutech.recipes.SintererRecipe;
import com.gluton.glutech.recipes.serializers.CrusherRecipeSerializer;
import com.gluton.glutech.recipes.serializers.SintererRecipeSerializer;
import com.gluton.glutech.tileentity.CrusherTileEntity;
import com.gluton.glutech.tileentity.EnergyCellTileEntity;
import com.gluton.glutech.tileentity.FurnaceGeneratorTileEntity;
import com.gluton.glutech.tileentity.SintererTileEntity;
import com.gluton.glutech.tools.ModToolMaterial;

import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.item.SwordItem;

/**
 * @author Gluton
 */
public class Registry extends RegistryHandler {
	
	private static final Supplier<Properties> TAB_GROUP = () -> new Item.Properties().group(GluTech.TAB);
	
	// Items
	public static final RegisteredItem<Item> GLUTONIUM_INGOT = RegisteredItem.create("glutonium_ingot");
	public static final RegisteredItem<Item> GLUTITE = RegisteredItem.create("glutite");
	public static final RegisteredItem<Item> GLUTITE_DUST = RegisteredItem.create("glutite_dust");
	public static final RegisteredItem<Item> ANCIENT_DEBRIS_POWDER = RegisteredItem.create("ancient_debris_powder");
//	public static final RegisteredItem<Item> GLUTITE_ANCIENT_DEBRIS_BLEND = RegisteredItem.create("glutite_ancient_debris_blend");
	
	// Tools
	public static final RegisteredItem<SwordItem> GLUTONIUM_SWORD = RegisteredItem.create("glutonium_sword",
			() -> new SwordItem(ModToolMaterial.GLUTONIUM, 7, -2.0f, TAB_GROUP.get()));
	
	// Armor
	public static final RegisteredItem<ArmorItem> GLUTONIUM_HELMET = RegisteredItem.create("glutonium_helmet",
			() -> new ArmorItem(ModArmorMaterial.GLUTONIUM, EquipmentSlotType.HEAD, TAB_GROUP.get()));
	public static final RegisteredItem<ArmorItem> GLUTONIUM_CHESTPLATE = RegisteredItem.create("glutonium_chestplate",
			() -> new ArmorItem(ModArmorMaterial.GLUTONIUM, EquipmentSlotType.CHEST, TAB_GROUP.get()));
	public static final RegisteredItem<ArmorItem> GLUTONIUM_LEGGINGS = RegisteredItem.create("glutonium_leggings",
			() -> new ArmorItem(ModArmorMaterial.GLUTONIUM, EquipmentSlotType.LEGS, TAB_GROUP.get()));
	public static final RegisteredItem<ArmorItem> GLUTONIUM_BOOTS = RegisteredItem.create("glutonium_boots",
			() -> new ArmorItem(ModArmorMaterial.GLUTONIUM, EquipmentSlotType.FEET, TAB_GROUP.get()));
	
	// Blocks
	public static final RegisteredBlock<GlutoniumBlock> GLUTONIUM_BLOCK = RegisteredBlock.create("glutonium_block", GlutoniumBlock::new);
	public static final RegisteredBlock<GlutiteOreBlock> GLUTITE_ORE = RegisteredBlock.create("glutite_ore", GlutiteOreBlock::new);
	
	// Tile Entities
	public static final RegisteredTileEntity<EnergyCellTileEntity> ENERGY_CELL = 
			RegisteredTileEntity.create("energy_cell", EnergyCellTileEntity::new, EnergyCellBlock::new, new BlockItemSupplier<EnergyCellItem>() {
				public <B extends Block> Supplier<EnergyCellItem> get(RegistryObject<B> block) { return () -> new EnergyCellItem(block.get()); }});
	
	// Containers
	public static final RegisteredContainer<FurnaceGeneratorContainer, FurnaceGeneratorTileEntity> FURNACE_GENERATOR = 
			RegisteredContainer.create("furnace_generator", FurnaceGeneratorContainer::new, FurnaceGeneratorTileEntity::new, FurnaceGeneratorBlock::new);
	
	// Recipe Serializers
	public static final RegisteredRecipeSerializer<CrusherRecipeSerializer, CrusherContainer, CrusherTileEntity> CRUSHER = 
			RegisteredRecipeSerializer.create("crusher", CrusherRecipe.RECIPE_ID, CrusherRecipeSerializer::new,
					CrusherContainer::new, CrusherTileEntity::new, CrusherBlock::new);
	public static final RegisteredRecipeSerializer<SintererRecipeSerializer, SintererContainer, SintererTileEntity> SINTERER = 
			RegisteredRecipeSerializer.create("sinterer", SintererRecipe.RECIPE_ID, SintererRecipeSerializer::new,
					SintererContainer::new, SintererTileEntity::new, SintererBlock::new);
	
	public static void init() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		ITEMS.register(bus);
		BLOCKS.register(bus);
		TILE_ENTITIES.register(bus);
		CONTAINERS.register(bus);
		RECIPE_SERIALIZERS.register(bus);
		
//		ITEMS.getEntries().stream().forEach(entry -> System.out.println(entry.getId()));
//		BLOCKS.getEntries().stream().forEach(entry -> System.out.println(entry.getId()));
	}
}
