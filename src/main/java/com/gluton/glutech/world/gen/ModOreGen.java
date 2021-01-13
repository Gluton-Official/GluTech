package com.gluton.glutech.world.gen;

import com.gluton.glutech.GluTech;
import com.gluton.glutech.registry.Registry;

import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author Gluton
 */
@Mod.EventBusSubscriber(modid = GluTech.MOD_ID)
public class ModOreGen {
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void generateOres(BiomeLoadingEvent event) {
		BiomeGenerationSettingsBuilder generation = event.getGeneration();

		if (event.getCategory() == Biome.Category.THEEND && !event.getName().equals(Biomes.THE_END.getLocation())) {
			generation.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
					Feature.NO_SURFACE_ORE.withConfiguration(new OreFeatureConfig(new BlockMatchRuleTest(Blocks.END_STONE),
					Registry.GLUTITE_ORE.getBlock().getDefaultState(), /*vein size*/ 2)).range(128).square().func_242731_b(/*frequency*/ 16));
		}
	}
}
