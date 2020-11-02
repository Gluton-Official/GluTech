package com.gluton.glutech;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gluton.glutech.util.RegistryHandler;
import com.gluton.glutech.world.gen.ModOreGen;

import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.MinecraftForge;
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
		
		RegistryHandler.init();
		
		MinecraftForge.EVENT_BUS.register(this);
		
		MinecraftForge.EVENT_BUS.register(new ModOreGen());
	}
	
	private void setup(final FMLCommonSetupEvent event) {
//		ModOreGen.registerOres();
	}
	
	private void doClientStuff(final FMLClientSetupEvent event) {
		
	}
}