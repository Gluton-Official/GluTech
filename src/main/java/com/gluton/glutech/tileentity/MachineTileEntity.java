package com.gluton.glutech.tileentity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.recipes.CachedRecipe;
import com.gluton.glutech.recipes.MachineRecipe;
import com.gluton.glutech.util.MachineItemHandler;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

/**
 * @author Gluton
 *
 */
public abstract class MachineTileEntity<R extends MachineRecipe> extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

	protected String name;
	protected ITextComponent customName;
	protected MachineItemHandler inventory;
	public int currentProcessTime;
	public final int maxProcessTime;
	private CachedRecipe<R> cachedRecipe;
	
	public MachineTileEntity(TileEntityType<?> tileEntityTypeIn, String name, int inventorySize, int maxProcessTime) {
		super(tileEntityTypeIn);
		this.name = name;
		this.inventory = new MachineItemHandler(inventorySize);
		this.maxProcessTime = maxProcessTime;
		this.cachedRecipe = null;
	}
	
	public void setCustomName(ITextComponent customName) {
		this.customName = customName;
	}
	
	private ITextComponent getDefaultName() {
		return new TranslationTextComponent("container." + GluTech.MOD_ID + "." + name);
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return this.customName != null ? this.customName : getDefaultName();
	}
	
	@Nullable
	public ITextComponent getCustomName() {
		return this.customName;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		if (nbt.contains("CustomName", Constants.NBT.TAG_STRING)) {
			this.customName = ITextComponent.Serializer.getComponentFromJson(nbt.getString("CustomName"));
		}
		
		NonNullList<ItemStack> inv = NonNullList.<ItemStack>withSize(this.inventory.getSlots(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(nbt, inv);
		this.inventory.setNonNullList(inv);
		
		this.currentProcessTime = nbt.getInt("CurrentProcessTime");
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		if (this.customName != null) {
			nbt.putString("CustomName", ITextComponent.Serializer.toJson(customName));
		}
		
		ItemStackHelper.saveAllItems(nbt, this.inventory.toNonNullList());
		
		nbt.putInt("CurrentProcessTime", this.currentProcessTime);
		
		return nbt;
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	protected R getRecipe(ItemStack ...stacks) {
		for (ItemStack stack : stacks) {
			if (stack == null) {
				return null;
			}
		}
		
		if (this.cachedRecipe != null && this.cachedRecipe.checkIngredients(stacks)) {
			return this.cachedRecipe.getRecipe();
		}
		
		Set<IRecipe<?>> recipes = findRecipesByType(getRecipeType(), this.world);
		for (IRecipe<?> irecipe : recipes) {
			R recipe = (R) irecipe;
			if (recipe.matches(new RecipeWrapper(this.inventory), this.world)) {
				this.cachedRecipe = new CachedRecipe<R>(recipe, stacks);
				return recipe;
			}
		}
		
		return null;
	}
	
	protected abstract IRecipeType<MachineRecipe> getRecipeType();
	
	public static Set<IRecipe<?>> findRecipesByType(IRecipeType<?> typeIn, World world) {
		return world != null ? world.getRecipeManager().getRecipes().stream()
				.filter(recipe -> recipe.getType() == typeIn).collect(Collectors.toSet()) : Collections.emptySet();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static Set<IRecipe<?>> findRecipesByType(IRecipeType<?> typeIn) {
		ClientWorld world = Minecraft.getInstance().world;
		return world != null ? world.getRecipeManager().getRecipes().stream()
				.filter(recipe -> recipe.getType() == typeIn).collect(Collectors.toSet()) : Collections.emptySet();
	}
	
	public static Set<ItemStack> getAllRecipeInputs(IRecipeType<?> typeIn, World worldIn) {
		Set<ItemStack> inputs = new HashSet<ItemStack>();
		Set<IRecipe<?>> recipes = findRecipesByType(typeIn, worldIn);
		for (IRecipe<?> recipe : recipes) {
			NonNullList<Ingredient> ingredients = recipe.getIngredients();
			ingredients.forEach(ingredient -> {
				for (ItemStack stack : ingredient.getMatchingStacks()) {
					inputs.add(stack);
				}
			});
		}
		return inputs;
	}
	
	protected boolean outputAvailable(MachineRecipe recipe, ItemStack outputStack) {
		if (outputStack.isEmpty()) {
			return true;
		}
		return Container.areItemsAndTagsEqual(recipe.getRecipeOutput(), outputStack)
				&& outputStack.getCount() < outputStack.getMaxStackSize();
	}
	
	public final IItemHandlerModifiable getInventory() {
		return this.inventory;
	}
	
	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);
		return new SUpdateTileEntityPacket(this.pos, 0, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.read(this.getBlockState(), pkt.getNbtCompound());
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);
		return nbt;
	}
	
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
		this.read(state, nbt);
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this.inventory));
	}
}
