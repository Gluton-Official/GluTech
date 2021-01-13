package com.gluton.glutech.registry;

import java.util.function.Supplier;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.items.BlockItemBase;
import com.gluton.glutech.items.ItemBase;
import com.gluton.glutech.recipes.Recipe;
import com.gluton.glutech.recipes.serializers.MachineRecipeSerializer;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Gluton
 */
public class RegistryHandler {
	
	protected static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GluTech.MOD_ID);
	protected static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GluTech.MOD_ID);
	protected static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, GluTech.MOD_ID);
	protected static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, GluTech.MOD_ID);
	protected static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, GluTech.MOD_ID);
	
	// TODO: add name field
	public static class RegisteredItem<I extends Item> {
		private RegistryObject<I> item;
		
		private RegisteredItem(RegistryObject<I> item) {
			this.item = item;
		}
		
		public I getItem() {
			return item.get();
		}

		protected static RegisteredItem<Item> create(String name) {
			return create(name, ItemBase::new);
		}
		
		protected static <I extends Item> RegisteredItem<I> create(String name, Supplier<I> supplier) {
			return new RegisteredItem<I>(ITEMS.register(name, supplier));
		}
	}
	
	public static class RegisteredBlock<B extends Block> {
		private RegistryObject<B> block;
		private RegistryObject<Item> item;
		
		private RegisteredBlock(RegistryObject<B> block, RegistryObject<Item> item) {
			this.block = block;
			this.item = item;
		}
		
		public B getBlock() {
			return block.get();
		}
		
		public Item getItem() {
			return item.get();
		}

		protected static <B extends Block> RegisteredBlock<B> create(String name, Supplier<B> supplier) {
			RegistryObject<B> block = BLOCKS.register(name, supplier);
			return new RegisteredBlock<B>(
					block,
					ITEMS.register(name, () -> new BlockItemBase(block.get())));
		}
		
		// TODO: fix itemSupplier to take in the registered block
		protected static <B extends Block, I extends Item> RegisteredBlock<B> create(
				String name, Supplier<B> blockSupplier, BlockItemSupplier<I> itemSupplier) {
			RegistryObject<B> block = BLOCKS.register(name, blockSupplier);
			return new RegisteredBlock<B>(
					block,
					ITEMS.register(name, itemSupplier.get(block)));
		}
	}
	
	public static class RegisteredTileEntity<T extends TileEntity> {
		private RegistryObject<TileEntityType<T>> tileEntity;
		private RegistryObject<Block> block;
		private RegistryObject<Item> item;
		
		private RegisteredTileEntity(RegistryObject<TileEntityType<T>> tileEntity, RegistryObject<Block> block, RegistryObject<Item> item) {
			this.tileEntity = tileEntity;
			this.block = block;
			this.item = item;
		}
		
		public T getTileEntity() {
			return tileEntity.get().create();
		}
		
		public TileEntityType<T> getTileEntityType() {
			return tileEntity.get();
		}
		
		public Block getBlock() {
			return block.get();
		}
		
		public Item getItem() {
			return item.get();
		}
		
		// TODO: figure out a neater combination of these two methods
		@SuppressWarnings("unchecked")
		protected static <T extends TileEntity, B extends Block> RegisteredTileEntity<T> create(
				String name, Supplier<T> tileEntitySupplier, Supplier<B> blockSupplier) {
			RegistryObject<B> block = BLOCKS.register(name, blockSupplier);
			return new RegisteredTileEntity<T>(
					TILE_ENTITIES.register(name, () -> TileEntityType.Builder.create(tileEntitySupplier, block.get()).build(null)),
					(RegistryObject<Block>) block,
					ITEMS.register(name, () -> new BlockItemBase(block.get())));
		}
		
		@SuppressWarnings("unchecked")
		protected static <T extends TileEntity, B extends Block, I extends Item> RegisteredTileEntity<T> create(
				String name, Supplier<T> tileEntitySupplier, Supplier<B> blockSupplier, BlockItemSupplier<I> itemSupplier) {
			RegistryObject<B> block = BLOCKS.register(name, blockSupplier);
			return new RegisteredTileEntity<T>(
					TILE_ENTITIES.register(name, () -> TileEntityType.Builder.create(tileEntitySupplier, block.get()).build(null)),
					(RegistryObject<Block>) block,
					ITEMS.register(name, itemSupplier.get(block)));
		}
	}
	
	public static class RegisteredContainer<C extends Container, T extends TileEntity> {
		private RegistryObject<ContainerType<C>> container;
		private RegistryObject<TileEntityType<T>> tileEntity;
		private RegistryObject<Block> block;
		private RegistryObject<Item> item;
		
		private RegisteredContainer(RegistryObject<ContainerType<C>> container, RegistryObject<TileEntityType<T>> tileEntity, 
				RegistryObject<Block> block, RegistryObject<Item> item) {
			this.container = container;
			this.tileEntity = tileEntity;
			this.block = block;
			this.item = item;
		}
		
		public C getContainer(final int windowId, final PlayerInventory player) {
			return container.get().create(windowId, player);
		}
		
		public ContainerType<C> getContainerType() {
			return container.get();
		}
		
		public T getTileEntity() {
			return tileEntity.get().create();
		}
		
		public TileEntityType<T> getTileEntityType() {
			return tileEntity.get();
		}
		
		public Block getBlock() {
			return block.get();
		}
		
		public Item getItem() {
			return item.get();
		}
		
		@SuppressWarnings("unchecked")
		protected static <C extends Container, T extends TileEntity, B extends Block> RegisteredContainer<C, T> create(
				String name, IContainerFactory<C> containerFactory, Supplier<T> tileEntitySupplier, Supplier<B> blockSupplier) {
			RegistryObject<B> block = BLOCKS.register(name, blockSupplier);
			return new RegisteredContainer<C, T>(
					CONTAINERS.register(name, () -> IForgeContainerType.create(containerFactory)),
					TILE_ENTITIES.register(name, () -> TileEntityType.Builder.create(tileEntitySupplier, block.get()).build(null)),
					(RegistryObject<Block>) block,
					ITEMS.register(name, () -> new BlockItemBase(block.get())));
		}
		
		@SuppressWarnings("unchecked")
		protected static <C extends Container, T extends TileEntity, B extends Block, I extends Item> RegisteredContainer<C, T> create(
				String name, IContainerFactory<C> containerFactory, Supplier<T> tileEntitySupplier, Supplier<B> blockSupplier, BlockItemSupplier<I> itemSupplier) {
			RegistryObject<B> block = BLOCKS.register(name, blockSupplier);
			return new RegisteredContainer<C, T>(
					CONTAINERS.register(name, () -> IForgeContainerType.create(containerFactory)),
					TILE_ENTITIES.register(name, () -> TileEntityType.Builder.create(tileEntitySupplier, block.get()).build(null)),
					(RegistryObject<Block>) block,
					ITEMS.register(name, itemSupplier.get(block)));
		}
	}
	
	public static class RegisteredRecipeSerializer<S extends MachineRecipeSerializer<? extends Recipe>, C extends Container, T extends TileEntity> {
		private IRecipeType<Recipe> recipeType;
		private RegistryObject<S> recipe;
		private RegistryObject<ContainerType<C>> container;
		private RegistryObject<TileEntityType<T>> tileEntity;
		private RegistryObject<Block> block;
		private RegistryObject<Item> item;
		
		private RegisteredRecipeSerializer(IRecipeType<Recipe> recipeType, RegistryObject<S> recipe, RegistryObject<ContainerType<C>> container, 
				RegistryObject<TileEntityType<T>> tileEntity, RegistryObject<Block> block, RegistryObject<Item> item) {
			this.recipeType = recipeType;
			this.recipe = recipe;
			this.container = container;
			this.tileEntity = tileEntity;
			this.block = block;
			this.item = item;
		}
		
		public IRecipeType<Recipe> getRecipeType() {
			return recipeType;
		}
		
		public S getRecipeSerializer() {
			return recipe.get();
		}
		
		public C getContainer(final int windowId, final PlayerInventory player) {
			return container.get().create(windowId, player);
		}
		
		public ContainerType<C> getContainerType() {
			return container.get();
		}
		
		public T getTileEntity() {
			return tileEntity.get().create();
		}
		
		public TileEntityType<T> getTileEntityType() {
			return tileEntity.get();
		}
		
		public Block getBlock() {
			return block.get();
		}
		
		public Item getItem() {
			return item.get();
		}
		
		@SuppressWarnings("unchecked")
		protected static <S extends MachineRecipeSerializer<? extends Recipe>, C extends Container, T extends TileEntity, B extends Block> RegisteredRecipeSerializer<S, C, T> create(
				String name, ResourceLocation recipeId, Supplier<S> recipeSupplier, IContainerFactory<C> containerFactory, Supplier<T> tileEntitySupplier, Supplier<B> blockSupplier) {
			RegistryObject<B> block = BLOCKS.register(name, blockSupplier);
			return new RegisteredRecipeSerializer<S, C, T>(
					registerType(recipeId),
					RECIPE_SERIALIZERS.register(name, recipeSupplier),
					CONTAINERS.register(name, () -> IForgeContainerType.create(containerFactory)),
					TILE_ENTITIES.register(name, () -> TileEntityType.Builder.create(tileEntitySupplier, block.get()).build(null)),
					(RegistryObject<Block>) block,
					ITEMS.register(name, () -> new BlockItemBase(block.get())));
		}
		
		// TODO: add abstract getRecipeId() to Recipe so RECIPE_ID can be pulled from the RecipeSerializer supplier if possible
		@SuppressWarnings("unchecked")
		protected static <S extends MachineRecipeSerializer<? extends Recipe>, C extends Container, T extends TileEntity, B extends Block, I extends Item> RegisteredRecipeSerializer<S, C, T> create(
				String name, ResourceLocation recipeId, Supplier<S> recipeSupplier, IContainerFactory<C> containerFactory, Supplier<T> tileEntitySupplier, Supplier<B> blockSupplier, BlockItemSupplier<I> itemSupplier) {
			RegistryObject<B> block = BLOCKS.register(name, blockSupplier);
			return new RegisteredRecipeSerializer<S, C, T>(
					registerType(recipeId),
					RECIPE_SERIALIZERS.register(name, recipeSupplier),
					CONTAINERS.register(name, () -> IForgeContainerType.create(containerFactory)),
					TILE_ENTITIES.register(name, () -> TileEntityType.Builder.create(tileEntitySupplier, block.get()).build(null)),
					(RegistryObject<Block>) block,
					ITEMS.register(name, itemSupplier.get(block)));
		}
	}
	
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
