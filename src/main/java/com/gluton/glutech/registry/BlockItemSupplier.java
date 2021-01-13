package com.gluton.glutech.registry;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;

/**
 * @author Gluton
 */
@FunctionalInterface
public interface BlockItemSupplier<T> {
	<B extends Block> Supplier<T> get(RegistryObject<B> block);
}
