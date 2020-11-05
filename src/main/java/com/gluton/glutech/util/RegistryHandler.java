package com.gluton.glutech.util;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.armor.ModArmorMaterial;
import com.gluton.glutech.blocks.BlockItemBase;
import com.gluton.glutech.blocks.CrusherBlock;
import com.gluton.glutech.blocks.GlutiteOre;
import com.gluton.glutech.blocks.GlutoniumBlock;
import com.gluton.glutech.container.CrusherContainer;
import com.gluton.glutech.items.ItemBase;
import com.gluton.glutech.recipes.CrusherRecipeSerializer;
import com.gluton.glutech.recipes.IMachineRecipe;
import com.gluton.glutech.tileentity.CrusherTileEntity;
import com.gluton.glutech.tools.ModToolMaterial;

import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
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
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, GluTech.MOD_ID);
	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, GluTech.MOD_ID);
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, GluTech.MOD_ID);
	
	public static void init() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		ITEMS.register(bus);
		BLOCKS.register(bus);
		TILE_ENTITIES.register(bus);
		CONTAINERS.register(bus);
		RECIPE_SERIALIZERS.register(bus);
	}
	
	// Items
	public static final RegistryObject<Item> GLUTONIUM_INGOT = ITEMS.register("glutonium_ingot", ItemBase::new);
	public static final RegistryObject<Item> GLUTITE = ITEMS.register("glutite", ItemBase::new);
	public static final RegistryObject<Item> GLUTITE_DUST = ITEMS.register("glutite_dust", ItemBase::new);
	public static final RegistryObject<Item> ANCIENT_DEBRIS_POWDER = ITEMS.register("ancient_debris_powder", ItemBase::new);
	public static final RegistryObject<Item> GLUTITE_ANCIENT_DEBRIS_BLEND = ITEMS.register("glutite_ancient_debris_blend", ItemBase::new);
	
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
	public static final RegistryObject<Block> CRUSHER_BLOCK = BLOCKS.register("crusher", CrusherBlock::new);
	
	// Block Items
	public static final RegistryObject<Item> GLUTONIUM_BLOCK_ITEM = ITEMS.register("glutonium_block", () -> new BlockItemBase(GLUTONIUM_BLOCK.get()));
	public static final RegistryObject<Item> GLUTITE_ORE_ITEM = ITEMS.register("glutite_ore", () -> new BlockItemBase(GLUTITE_ORE.get()));
	public static final RegistryObject<Item> CRUSHER_ITEM = ITEMS.register("crusher", () -> new BlockItemBase(CRUSHER_BLOCK.get()));
	
	// Tile Entities
	public static final RegistryObject<TileEntityType<CrusherTileEntity>> CRUSHER = TILE_ENTITIES.register("crusher",
			()  -> TileEntityType.Builder.create(CrusherTileEntity::new, CRUSHER_BLOCK.get()).build(null));
	
	// Containers
	public static final RegistryObject<ContainerType<CrusherContainer>> CRUSHER_CONTAINER = CONTAINERS.register("crusher",
			() -> IForgeContainerType.create(CrusherContainer::new));
	
	// Recipe Serializers
	public static final RegistryObject<CrusherRecipeSerializer> CRUSHER_SERIALIZER = RECIPE_SERIALIZERS.register("crusher", CrusherRecipeSerializer::new);
	public static final IRecipeType<IMachineRecipe> MACHINE_TYPE = registerType(IMachineRecipe.RECIPE_TYPE_ID);
	
	
	private static class RecipeType<T extends IRecipe<?>> implements IRecipeType<T> {
		@Override
		public String toString() {
			return Registry.RECIPE_TYPE.getKey(this).toString();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends IRecipeType<?>> T registerType(ResourceLocation recipeTypeId) {
		return (T) Registry.register(Registry.RECIPE_TYPE, recipeTypeId, new RecipeType<>());
	}
}
