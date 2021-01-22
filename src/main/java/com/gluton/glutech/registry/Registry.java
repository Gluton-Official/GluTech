package com.gluton.glutech.registry;

import com.gluton.glutech.blocks.CrusherBlock;
import com.gluton.glutech.blocks.EnergyCellBlock;
import com.gluton.glutech.blocks.FurnaceGeneratorBlock;
import com.gluton.glutech.blocks.GlutiteOreBlock;
import com.gluton.glutech.blocks.GlutoniumBlock;
import com.gluton.glutech.blocks.SintererBlock;
import com.gluton.glutech.container.CrusherContainer;
import com.gluton.glutech.container.EnergyCellContainer;
import com.gluton.glutech.container.FurnaceGeneratorContainer;
import com.gluton.glutech.container.SintererContainer;
import com.gluton.glutech.items.EnergyArmorItem;
import com.gluton.glutech.items.EnergyBlockItem;
import com.gluton.glutech.items.EnergyCellItem;
import com.gluton.glutech.items.EnergySwordItem;
import com.gluton.glutech.items.materials.EnergyArmorMaterial;
import com.gluton.glutech.items.materials.EnergyItemMaterial;
import com.gluton.glutech.recipes.serializers.CrusherRecipeSerializer;
import com.gluton.glutech.recipes.serializers.SintererRecipeSerializer;
import com.gluton.glutech.tileentity.CrusherTileEntity;
import com.gluton.glutech.tileentity.EnergyCellTileEntity;
import com.gluton.glutech.tileentity.FurnaceGeneratorTileEntity;
import com.gluton.glutech.tileentity.SintererTileEntity;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author Gluton
 */
public class Registry extends RegistryHandler {
	
//	private static final Supplier<Properties> TAB_GROUP = () -> new Item.Properties().group(GluTech.TAB);
	
	// Items
	public static final RegisteredItem<Item> GLUTONIUM_INGOT = RegisteredItem.create("glutonium_ingot");
	public static final RegisteredItem<Item> GLUTITE = RegisteredItem.create("glutite");
	public static final RegisteredItem<Item> GLUTITE_DUST = RegisteredItem.create("glutite_dust");
	public static final RegisteredItem<Item> ANCIENT_DEBRIS_POWDER = RegisteredItem.create("ancient_debris_powder");
//	public static final RegisteredItem<Item> GLUTITE_ANCIENT_DEBRIS_BLEND = RegisteredItem.create("glutite_ancient_debris_blend");
	
	// Tools
	public static final RegisteredItem<EnergySwordItem> GLUTONIUM_SWORD = RegisteredItem.create("glutonium_sword",
			() -> new EnergySwordItem(EnergyItemMaterial.GLUTONIUM, 7, -2.0f));
	
	// Armor
	public static final RegisteredItem<EnergyArmorItem> GLUTONIUM_HELMET = RegisteredItem.create("glutonium_helmet",
			() -> new EnergyArmorItem(EnergyArmorMaterial.GLUTONIUM, EquipmentSlotType.HEAD));
	public static final RegisteredItem<EnergyArmorItem> GLUTONIUM_CHESTPLATE = RegisteredItem.create("glutonium_chestplate",
			() -> new EnergyArmorItem(EnergyArmorMaterial.GLUTONIUM, EquipmentSlotType.CHEST));
	public static final RegisteredItem<EnergyArmorItem> GLUTONIUM_LEGGINGS = RegisteredItem.create("glutonium_leggings",
			() -> new EnergyArmorItem(EnergyArmorMaterial.GLUTONIUM, EquipmentSlotType.LEGS));
	public static final RegisteredItem<EnergyArmorItem> GLUTONIUM_BOOTS = RegisteredItem.create("glutonium_boots",
			() -> new EnergyArmorItem(EnergyArmorMaterial.GLUTONIUM, EquipmentSlotType.FEET));
	
	// Blocks
	public static final RegisteredBlock<GlutoniumBlock, BlockItem> GLUTONIUM_BLOCK = RegisteredBlock.create("glutonium_block", GlutoniumBlock::new);
	public static final RegisteredBlock<GlutiteOreBlock, BlockItem> GLUTITE_ORE = RegisteredBlock.create("glutite_ore", GlutiteOreBlock::new);
	
	// Tile Entities
	
	// Containers
	public static final RegisteredContainer<EnergyCellContainer, EnergyCellTileEntity, EnergyCellBlock, EnergyCellItem> ENERGY_CELL = 
			RegisteredContainer.create("energy_cell", EnergyCellContainer::new, EnergyCellTileEntity::new, EnergyCellBlock::new,
					block -> () -> new EnergyCellItem(block.get()));
	public static final RegisteredContainer<FurnaceGeneratorContainer, FurnaceGeneratorTileEntity, FurnaceGeneratorBlock, BlockItem> FURNACE_GENERATOR = 
			RegisteredContainer.create("furnace_generator", FurnaceGeneratorContainer::new, FurnaceGeneratorTileEntity::new, FurnaceGeneratorBlock::new,
					block -> () -> new EnergyBlockItem(block.get(), FurnaceGeneratorTileEntity.CAPACITY));
	
	// Recipe Serializers
	public static final RegisteredRecipeSerializer<CrusherRecipeSerializer, CrusherContainer, CrusherTileEntity, CrusherBlock, BlockItem> CRUSHER = 
			RegisteredRecipeSerializer.create("crusher", CrusherRecipeSerializer::new, CrusherContainer::new, CrusherTileEntity::new, CrusherBlock::new,
					block -> () -> new EnergyBlockItem(block.get(), CrusherTileEntity.CAPACITY));
	public static final RegisteredRecipeSerializer<SintererRecipeSerializer, SintererContainer, SintererTileEntity, SintererBlock, BlockItem> SINTERER = 
			RegisteredRecipeSerializer.create("sinterer", SintererRecipeSerializer::new, SintererContainer::new, SintererTileEntity::new, SintererBlock::new,
					block -> () -> new EnergyBlockItem(block.get(), SintererTileEntity.CAPACITY));
	
	public static void init() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		ITEMS.register(bus);
		BLOCKS.register(bus);
		TILE_ENTITIES.register(bus);
		CONTAINERS.register(bus);
		RECIPE_SERIALIZERS.register(bus);
	}
}
