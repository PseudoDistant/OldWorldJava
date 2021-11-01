package io.github.pseudodistant.oldworldjava;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.Feature;

import java.util.List;
import java.util.function.Supplier;

public class OldWorldGen extends NoiseChunkGenerator {
    protected ChunkRandom chunkRandom;
    protected Supplier<ChunkGeneratorSettings> settings;
    protected long seed;
    protected BiomeSource biomeSource;
    protected final int limitedWorldDepth;
    protected final int limitedWorldWidth;
    protected final List<ConfiguredFeature<?, ?>> features = ImmutableList.<ConfiguredFeature<?, ?>>of(
            ConfiguredFeatures.ORE_ANDESITE,
            ConfiguredFeatures.ORE_DEEPSLATE,
            ConfiguredFeatures.ORE_DIORITE,
            ConfiguredFeatures.ORE_GRANITE,
            ConfiguredFeatures.ORE_REDSTONE,
            ConfiguredFeatures.ORE_CLAY,
            ConfiguredFeatures.ORE_COAL,
            ConfiguredFeatures.ORE_COPPER,
            ConfiguredFeatures.ORE_DIAMOND,
            ConfiguredFeatures.ORE_DIRT,
            ConfiguredFeatures.ORE_EMERALD,
            ConfiguredFeatures.ORE_GOLD,
            ConfiguredFeatures.ORE_GRANITE,
            ConfiguredFeatures.ORE_GRAVEL,
            ConfiguredFeatures.ORE_INFESTED,
            ConfiguredFeatures.ORE_IRON,
            ConfiguredFeatures.ORE_LAPIS,
            ConfiguredFeatures.ORE_REDSTONE,
            ConfiguredFeatures.ORE_TUFF
    );

    public static final Codec<OldWorldGen> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource),
                    Codec.LONG.fieldOf("seed").forGetter((generator) -> generator.seed),
                    Codec.INT.fieldOf("LimitedWorldDepth").forGetter((generator) -> generator.limitedWorldDepth),
                    Codec.INT.fieldOf("LimitedWorldWidth").forGetter((generator) -> generator.limitedWorldWidth),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter((generator) -> generator.settings))
            .apply(instance, instance.stable(OldWorldGen::new)));

    @Override protected Codec<? extends NoiseChunkGenerator> getCodec() {
        return OldWorldGen.CODEC;
    }

    public OldWorldGen(BiomeSource biomeSource, long seed, int LWD, int LWL, Supplier<ChunkGeneratorSettings> settings) {
        super(biomeSource,seed,settings);
        this.seed = seed;
        this.settings = settings;
        this.biomeSource = biomeSource;
        this.chunkRandom = new ChunkRandom(seed);
        this.limitedWorldDepth = LWD;
        this.limitedWorldWidth = LWL;
    }
    @Override public void carve(long seed, BiomeAccess access, Chunk chunk, GenerationStep.Carver carver) {}

    @Override public NoiseChunkGenerator withSeed(long seed) {return new OldWorldGen(this.biomeSource.withSeed(seed), seed, this.limitedWorldDepth, this.limitedWorldWidth, settings);}

    @Override
    public void buildSurface(ChunkRegion region, Chunk chunk) {
        //TODO: Do something cool with this, maybe. Might not actually be needed.
        super.buildSurface(region, chunk);
    }

    //@Override public List<List<Supplier<ConfiguredFeature<?, ?>>>> getFeatures() {return this.features;}

    @Override
    public void generateFeatures(ChunkRegion region, StructureAccessor accessor) {
        BlockPos chunkCenter = new BlockPos(region.getCenterPos().x * 16, 0, region.getCenterPos().z * 16);
        BlockPos.Mutable current = new BlockPos.Mutable();
        ChunkPos chunkPos = region.getCenterPos();
        int ecks = chunkPos.getStartX();
        int zee = chunkPos.getStartZ();
        BlockPos blockPos = new BlockPos(ecks, region.getBottomY(), zee);
        Biome biome = this.populationSource.getBiomeForNoiseGen(chunkPos);
        ChunkRandom chunkRandom = new ChunkRandom();
        long l = chunkRandom.setPopulationSeed(region.getSeed(), ecks, zee);
        if (blockPos.getX() >= -(this.limitedWorldWidth / 2) && blockPos.getX() <= (this.limitedWorldWidth / 2) - 8) {
            if (blockPos.getZ() >= -(this.limitedWorldDepth / 2) && blockPos.getZ() <= (this.limitedWorldDepth / 2) - 8) {
                try {
                    biome.generateFeatureStep(accessor, this, region, l, chunkRandom, blockPos);
                } catch (Exception e) {
                    CrashReport crashReport = CrashReport.create(e, "Biome decoration");
                    crashReport.addElement("Generation").add("CenterX", chunkPos.x).add("CenterZ", chunkPos.z).add("Seed", l).add("Biome", biome);
                    throw new CrashException(crashReport);
                }
            }
        }
        for (final BlockPos pos : BlockPos.iterate(chunkCenter.getX(), 0, chunkCenter.getZ(), chunkCenter.getX() + 15, 0, chunkCenter.getZ() + 15)) {
            current.set(pos);
            for (int y = 0; y <= 140; y++) {
                if (pos.getX() >= -(this.limitedWorldWidth / 2) && pos.getX() <= (this.limitedWorldWidth / 2) - 1) {
                    if (pos.getZ() >= -(this.limitedWorldDepth / 2) && pos.getZ() <= (this.limitedWorldDepth / 2) - 1) {
                        continue;
                    }
                }
                current.set(pos.getX(), y, pos.getZ());
                BlockState blockState = Blocks.BARRIER.getDefaultState();
                region.setBlockState(current.set(pos.getX(), y, pos.getZ()), blockState, 0);
            }
        }
    }

    @Override
    public int getWorldHeight() {
        return 127;
    }
}
