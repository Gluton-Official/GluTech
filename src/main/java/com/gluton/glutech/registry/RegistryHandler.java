package com.gluton.glutech.registry;

import java.util.function.Function;
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
import net.minecraft.item.BlockItem;
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
	
	public static class RegisteredItem<I extends Item> implements ItemProvider<I> {
		private String name;
		private RegistryObject<I> item;
		
		private RegisteredItem(String name, RegistryObject<I> item) {
			this.name = name;
			this.item = item;
		}

		public String getName() {
			return this.name;
		}
		
		@Override
		public I getItem() {
			return this.item.get();
		}
		
		@Override
		public RegistryObject<I> getItemObject() {
			return this.item;
		}

		protected static RegisteredItem<Item> create(String name) {
			return create(name, ItemBase::new);
		}
		
		protected static <I extends Item> RegisteredItem<I> create(String name, Supplier<I> supplier) {
			return new RegisteredItem<I>(name, ITEMS.register(name, supplier));
		}
	}

	public static class RegisteredBlock<B extends Block, I extends BlockItem> implements BlockProvider<B>, ItemProvider<I>{
		private String name;
		private RegistryObject<B> block;
		private RegistryObject<I> item;
		
		private RegisteredBlock(String name, RegistryObject<B> block, RegistryObject<I> item) {
			this.name = name;
			this.block = block;
			this.item = item;
		}
		
		public String getName() {
			return this.name;
		}
		
		@Override
		public B getBlock() {
			return this.block.get();
		}
		
		@Override
		public I getItem() {
			return this.item.get();
		}
		
		@Override
		public RegistryObject<B> getBlockObject() { 
			return this.block;
		}
		
		@Override
		public RegistryObject<I> getItemObject() {
			return this.item;
		}

		protected static <B extends Block> RegisteredBlock<B, BlockItem> create(String name, Supplier<B> supplier) {
			RegistryObject<B> block = BLOCKS.register(name, supplier);
			return new RegisteredBlock<B, BlockItem>(name,
					block,
					ITEMS.register(name, () -> new BlockItemBase(block.get())));
		}
		
		protected static <B extends Block, I extends BlockItem> RegisteredBlock<B, I> create(
				String name, Supplier<B> blockSupplier, Function<Block, Supplier<I>> itemFunction) {
			RegistryObject<B> block = BLOCKS.register(name, blockSupplier);
			return new RegisteredBlock<B, I>(name,
					block,
					ITEMS.register(name, itemFunction.apply(block.get())));
		}
	}
	
	public static class RegisteredTileEntity<T extends TileEntity, B extends Block, I extends BlockItem> 
			implements TileEntityProvider<T>, BlockProvider<B>, ItemProvider<I> {
		private String name;
		private RegistryObject<TileEntityType<T>> tileEntityType;
		private RegistryObject<B> block;
		private RegistryObject<I> item;
		
		private RegisteredTileEntity(String name, RegistryObject<TileEntityType<T>> tileEntityType, 
				RegistryObject<B> block, RegistryObject<I> item) {
			this.name = name;
			this.tileEntityType = tileEntityType;
			this.block = block;
			this.item = item;
		}
		
		public String getName() {
			return this.name;
		}
		
		@Override
		public T getTileEntity() {
			return this.tileEntityType.get().create();
		}
		
		@Override
		public TileEntityType<T> getTileEntityType() {
			return this.tileEntityType.get();
		}
		
		@Override
		public B getBlock() {
			return this.block.get();
		}
		
		@Override
		public I getItem() {
			return this.item.get();
		}
		
		@Override
		public RegistryObject<TileEntityType<T>> getTileEntityTypeObject() {
			return this.tileEntityType;
		}
		
		@Override
		public RegistryObject<B> getBlockObject() { 
			return this.block;
		}
		
		@Override
		public RegistryObject<I> getItemObject() {
			return this.item;
		}
		
		protected static <T extends TileEntity, B extends Block> RegisteredTileEntity<T, B, BlockItem> create(
				String name, Supplier<T> tileEntitySupplier, Supplier<B> blockSupplier) {
			RegistryObject<B> block = BLOCKS.register(name, blockSupplier);
			return new RegisteredTileEntity<T, B, BlockItem>(name, TILE_ENTITIES.register(name,
					() -> TileEntityType.Builder.create(tileEntitySupplier, block.get()).build(null)),
					block,
					ITEMS.register(name, () -> new BlockItemBase(block.get())));
		}
		
		protected static <T extends TileEntity, B extends Block, I extends BlockItem> RegisteredTileEntity<T, B, I> create(
				String name, Supplier<T> tileEntitySupplier, Supplier<B> blockSupplier, 
				Function<RegistryObject<B>, Supplier<I>> itemFunction) {
			RegistryObject<B> block = BLOCKS.register(name, blockSupplier);
			return new RegisteredTileEntity<T, B, I>(name, TILE_ENTITIES.register(name,
					() -> TileEntityType.Builder.create(tileEntitySupplier, block.get()).build(null)),
					block,
					ITEMS.register(name, itemFunction.apply(block)));
		}
	}
	
	public static class RegisteredContainer<C extends Container, T extends TileEntity, B extends Block, I extends BlockItem>
			implements ContainerProvider<C>, TileEntityProvider<T>, BlockProvider<B>, ItemProvider<I> {
		private String name;
		private RegistryObject<ContainerType<C>> containerType;
		private RegistryObject<TileEntityType<T>> tileEntityType;
		private RegistryObject<B> block;
		private RegistryObject<I> item;
		
		private RegisteredContainer(String name, RegistryObject<ContainerType<C>> containerType,
				RegistryObject<TileEntityType<T>> tileEntityType, RegistryObject<B> block, RegistryObject<I> item) {
			this.name = name;
			this.containerType = containerType;
			this.tileEntityType = tileEntityType;
			this.block = block;
			this.item = item;
		}
		
		public String getName() {
			return this.name;
		}
		
		@Override
		public C getContainer(final int windowId, final PlayerInventory player) {
			return this.containerType.get().create(windowId, player);
		}
		
		@Override
		public ContainerType<C> getContainerType() {
			return this.containerType.get();
		}
		
		@Override
		public T getTileEntity() {
			return this.tileEntityType.get().create();
		}
		
		@Override
		public TileEntityType<T> getTileEntityType() {
			return this.tileEntityType.get();
		}
		
		@Override
		public B getBlock() {
			return this.block.get();
		}
		
		@Override
		public I getItem() {
			return this.item.get();
		}
		
		@Override
		public RegistryObject<ContainerType<C>> getContainerTypeObject() {
			return this.containerType;
		}
		
		@Override
		public RegistryObject<TileEntityType<T>> getTileEntityTypeObject() {
			return this.tileEntityType;
		}
		
		@Override
		public RegistryObject<B> getBlockObject() { 
			return this.block;
		}
		
		@Override
		public RegistryObject<I> getItemObject() {
			return this.item;
		}
		
		protected static <C extends Container, T extends TileEntity, B extends Block> 
				RegisteredContainer<C, T, B, BlockItem> create(String name, IContainerFactory<C> containerFactory,
				Supplier<T> tileEntitySupplier, Supplier<B> blockSupplier) {
			RegistryObject<B> block = BLOCKS.register(name, blockSupplier);
			return new RegisteredContainer<C, T, B, BlockItem>(name,
					CONTAINERS.register(name, () -> IForgeContainerType.create(containerFactory)),
					TILE_ENTITIES.register(name, () -> TileEntityType.Builder.create(tileEntitySupplier, block.get()).build(null)),
					block,
					ITEMS.register(name, () -> new BlockItemBase(block.get())));
		}
		
		protected static <C extends Container, T extends TileEntity, B extends Block, I extends BlockItem>
				RegisteredContainer<C, T, B, I> create(String name, IContainerFactory<C> containerFactory, 
				Supplier<T> tileEntitySupplier, Supplier<B> blockSupplier, Function<RegistryObject<B>, Supplier<I>> itemFunction) {
			RegistryObject<B> block = BLOCKS.register(name, blockSupplier);
			return new RegisteredContainer<C, T, B, I>(name,
					CONTAINERS.register(name, () -> IForgeContainerType.create(containerFactory)),
					TILE_ENTITIES.register(name, () -> TileEntityType.Builder.create(tileEntitySupplier, block.get()).build(null)),
					block,
					ITEMS.register(name, itemFunction.apply(block)));
		}
	}
	
	public static class RegisteredRecipeSerializer<S extends MachineRecipeSerializer<? extends Recipe>, C extends Container,
			T extends TileEntity, B extends Block, I extends BlockItem> implements RecipeSerializerProvider<S>,
			ContainerProvider<C>, TileEntityProvider<T>, BlockProvider<B>, ItemProvider<I> {
		private String name;
		private IRecipeType<Recipe> recipeType;
		private RegistryObject<S> recipeSerializer;
		private RegistryObject<ContainerType<C>> containerType;
		private RegistryObject<TileEntityType<T>> tileEntityType;
		private RegistryObject<B> block;
		private RegistryObject<I> item;
		
		private RegisteredRecipeSerializer(String name, IRecipeType<Recipe> recipeType, RegistryObject<S> recipeSerializer,
				RegistryObject<ContainerType<C>> containerType, RegistryObject<TileEntityType<T>> tileEntityType,
				RegistryObject<B> block, RegistryObject<I> item) {
			this.name = name;
			this.recipeType = recipeType;
			this.recipeSerializer = recipeSerializer;
			this.containerType = containerType;
			this.tileEntityType = tileEntityType;
			this.block = block;
			this.item = item;
		}
		
		public String getName() {
			return this.name;
		}
		
		@Override
		public IRecipeType<Recipe> getRecipeType() {
			return this.recipeType;
		}
		
		@Override
		public S getRecipeSerializer() {
			return this.recipeSerializer.get();
		}
		
		@Override
		public C getContainer(final int windowId, final PlayerInventory player) {
			return this.containerType.get().create(windowId, player);
		}
		
		@Override
		public ContainerType<C> getContainerType() {
			return this.containerType.get();
		}
		
		@Override
		public T getTileEntity() {
			return this.tileEntityType.get().create();
		}
		
		@Override
		public TileEntityType<T> getTileEntityType() {
			return this.tileEntityType.get();
		}
		
		@Override
		public B getBlock() {
			return this.block.get();
		}
		
		@Override
		public I getItem() {
			return this.item.get();
		}
		
		@Override
		public RegistryObject<I> getItemObject() {
			return this.item;
		}

		@Override
		public RegistryObject<B> getBlockObject() {
			return this.block;
		}

		@Override
		public RegistryObject<TileEntityType<T>> getTileEntityTypeObject() {
			return this.tileEntityType;
		}

		@Override
		public RegistryObject<ContainerType<C>> getContainerTypeObject() {
			return this.containerType;
		}

		@Override
		public RegistryObject<S> getRecipeSerializerObject() {
			return this.recipeSerializer;
		}
		
		protected static <S extends MachineRecipeSerializer<? extends Recipe>, C extends Container, T extends TileEntity,
				B extends Block> RegisteredRecipeSerializer<S, C, T, B, BlockItem> create(
				String name, Supplier<S> recipeSupplier, IContainerFactory<C> containerFactory,
				Supplier<T> tileEntitySupplier, Supplier<B> blockSupplier) {
			RegistryObject<B> block = BLOCKS.register(name, blockSupplier);
			return new RegisteredRecipeSerializer<S, C, T, B, BlockItem>(name,
					registerType(recipeSupplier.get().getRecipeId()),
					RECIPE_SERIALIZERS.register(name, recipeSupplier),
					CONTAINERS.register(name, () -> IForgeContainerType.create(containerFactory)),
					TILE_ENTITIES.register(name, () -> TileEntityType.Builder.create(tileEntitySupplier, block.get()).build(null)),
					block,
					ITEMS.register(name, () -> new BlockItemBase(block.get())));
		}
		
		// TODO: add abstract getRecipeId() to Recipe so RECIPE_ID can be pulled from the RecipeSerializer supplier if possible
		protected static <S extends MachineRecipeSerializer<? extends Recipe>, C extends Container, T extends TileEntity,
				B extends Block, I extends BlockItem> RegisteredRecipeSerializer<S, C, T, B, I> create(
				String name, Supplier<S> recipeSupplier, IContainerFactory<C> containerFactory,
				Supplier<T> tileEntitySupplier, Supplier<B> blockSupplier, Function<RegistryObject<B>, Supplier<I>> itemFunction) {
			RegistryObject<B> block = BLOCKS.register(name, blockSupplier);
			return new RegisteredRecipeSerializer<S, C, T, B, I>(name,
					registerType(recipeSupplier.get().getRecipeId()),
					RECIPE_SERIALIZERS.register(name, recipeSupplier),
					CONTAINERS.register(name, () -> IForgeContainerType.create(containerFactory)),
					TILE_ENTITIES.register(name, () -> TileEntityType.Builder.create(tileEntitySupplier, block.get()).build(null)),
					block,
					ITEMS.register(name, itemFunction.apply(block)));
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
	
	public static interface ItemProvider<I extends Item> {
		I getItem();
		RegistryObject<I> getItemObject();
	}
	
	public static interface BlockProvider<B extends Block> {
		B getBlock();
		RegistryObject<B> getBlockObject();
	}
	
	public static interface TileEntityProvider<T extends TileEntity> {
		T getTileEntity();
		RegistryObject<TileEntityType<T>> getTileEntityTypeObject();
		TileEntityType<T> getTileEntityType();
		
	}
	
	public static interface ContainerProvider<C extends Container> {
		C getContainer(final int windowId, final PlayerInventory player);
		ContainerType<C> getContainerType();
		RegistryObject<ContainerType<C>> getContainerTypeObject();
	}
	
	public static interface RecipeSerializerProvider<S extends MachineRecipeSerializer<? extends Recipe>> {
		S getRecipeSerializer();
		IRecipeType<? extends Recipe> getRecipeType();
		RegistryObject<S> getRecipeSerializerObject();
	}
}
