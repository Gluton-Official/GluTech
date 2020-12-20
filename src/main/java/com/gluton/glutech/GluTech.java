package com.gluton.glutech;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gluton.glutech.client.gui.CrusherScreen;
import com.gluton.glutech.client.gui.FurnaceGeneratorScreen;
import com.gluton.glutech.client.gui.SintererScreen;
import com.gluton.glutech.client.renderer.EnergyCellRenderer;
import com.gluton.glutech.util.RegistryHandler;
import com.gluton.glutech.world.gen.ModOreGen;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author Gluton
 */
@Mod(GluTech.MOD_ID)
public class GluTech {
	
	public static final String MOD_ID = "glutech";
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	public static final ItemGroup TAB = new GluTechTab();
	
	public GluTech() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onTexturePreStitch);
		
		RegistryHandler.init();
		
		MinecraftForge.EVENT_BUS.register(this);
		
		MinecraftForge.EVENT_BUS.register(new ModOreGen());
	}
	
	private void setup(final FMLCommonSetupEvent event) {

	}
	
	private void doClientStuff(final FMLClientSetupEvent event) {
		ScreenManager.registerFactory(RegistryHandler.FURNACE_GENERATOR_CONTAINER.get(), FurnaceGeneratorScreen::new);
		ScreenManager.registerFactory(RegistryHandler.CRUSHER_CONTAINER.get(), CrusherScreen::new);
		ScreenManager.registerFactory(RegistryHandler.SINTERER_CONTAINER.get(), SintererScreen::new);
		
		ClientRegistry.bindTileEntityRenderer(RegistryHandler.ENERGY_CELL.get(), EnergyCellRenderer::new);
	}
	
	private void onTexturePreStitch(TextureStitchEvent.Pre event) {
		if (!event.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE)) {
			return;
		}
		
		for (ResourceLocation texture : EnergyCellRenderer.IO_TEXTURES.values()) {
			event.addSprite(texture);
		}
	}
}