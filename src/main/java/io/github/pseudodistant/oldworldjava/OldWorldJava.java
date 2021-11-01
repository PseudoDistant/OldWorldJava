package io.github.pseudodistant.oldworldjava;

import io.github.pseudodistant.oldworldjava.mixin.GeneratorTypeMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldWorldJava implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("oldworldjava");

	@Environment(EnvType.CLIENT)
	public static final GeneratorType OldWorldJava = new GeneratorType("oldworldjava") {
		@Override
		public ChunkGenerator getChunkGenerator(Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry, long seed) {
			return new OldWorldGen(new OldLayeredBiomeSource(seed, biomeRegistry),
					seed, 256, 256,
					() -> chunkGeneratorSettingsRegistry.getOrThrow(ChunkGeneratorSettings.OVERWORLD));
		}
	};
	@Override
	public void onInitialize() {
		GeneratorTypeMixin.getValues().add(OldWorldJava);
		Registry.register(Registry.CHUNK_GENERATOR, new Identifier("oldworldjava","oldworldjava"), OldWorldGen.CODEC);
	}
}
