package archives.tater.waxyvision.datagen;

import archives.tater.waxyvision.OverlayModels;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopperCollection;

import com.google.common.collect.Streams;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class OverlayModelGenerator extends FabricCodecDataProvider<OverlayModels.UnbakedEntry> {

    public OverlayModelGenerator(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(dataOutput, registriesFuture, PackOutput.Target.RESOURCE_PACK, OverlayModels.PATH, OverlayModels.UnbakedEntry.CODEC);
    }

    private static final List<String> WEATHER_STEPS = List.of("", "exposed_", "weathered_", "oxidized_");

    private static List<Block> toList(WeatheringCopperCollection.ByState<Block> blocks) {
        return toStream(blocks).toList();
    }

    private static Stream<Block> toStream(WeatheringCopperCollection.ByState<Block> blocks) {
        return Stream.of(blocks.unaffected(), blocks.exposed(), blocks.weathered(), blocks.oxidized());
    }

    private static Stream<Block> waxed(WeatheringCopperCollection<Block> blocks) {
        return toStream(blocks.waxed());
    }

    private static Stream<Identifier> prefixed(Identifier... blocks) {
        return Arrays.stream(blocks)
                .flatMap(id -> WEATHER_STEPS.stream().map(step -> id.withPrefix("waxed_" + step)));
    }

    private static Stream<Identifier> getIds(Stream<Block> blocks) {
        return blocks.map(BuiltInRegistries.BLOCK::getKey);
    }

    private static void registerRaw(BiConsumer<Identifier, OverlayModels.UnbakedEntry> consumer, Block modelBlock, List<Identifier> blocks) {
        var id = BuiltInRegistries.BLOCK.getKey(modelBlock);
        consumer.accept(id, new OverlayModels.UnbakedEntry(id, blocks));
    }

    private static void registerRaw(BiConsumer<Identifier, OverlayModels.UnbakedEntry> consumer, Block modelBlock, Stream<Identifier> blocks) {
        registerRaw(consumer, modelBlock, blocks.toList());
    }

    private static void register(BiConsumer<Identifier, OverlayModels.UnbakedEntry> consumer, Block modelBlock, Stream<Block> blocks) {
        registerRaw(consumer, modelBlock, getIds(blocks));
    }

    private static void register(BiConsumer<Identifier, OverlayModels.UnbakedEntry> consumer, Block modelBlock, List<Block> blocks) {
        register(consumer, modelBlock, blocks.stream());
    }

    private static void register(BiConsumer<Identifier, OverlayModels.UnbakedEntry> consumer, Block modelBlock, Block... blocks) {
        register(consumer, modelBlock, Arrays.stream(blocks));
    }

    private static void registerRawPrefixed(BiConsumer<Identifier, OverlayModels.UnbakedEntry> consumer, Block modelBlock, Identifier... blocks) {
            registerRaw(consumer, modelBlock, prefixed(blocks));
    }

    private static Identifier maglev(String path) {
        return Identifier.fromNamespaceAndPath("maglev", path);
    }

    private static Identifier copperierAge(String path) {
        return Identifier.fromNamespaceAndPath("thecopperierage", path);
    }

    @Override
    protected void configure(BiConsumer<Identifier, OverlayModels.UnbakedEntry> biConsumer, HolderLookup.Provider provider) {
        register(biConsumer, FakeBlocks.CUBE, Streams.concat(
                waxed(Blocks.COPPER_BLOCK),
                waxed(Blocks.CUT_COPPER),
                waxed(Blocks.CHISELED_COPPER),
                waxed(Blocks.COPPER_GRATE),
                waxed(Blocks.COPPER_BULB)
        ));
        register(biConsumer, FakeBlocks.STAIRS, waxed(Blocks.CUT_COPPER_STAIRS));
        register(biConsumer, FakeBlocks.SLAB, waxed(Blocks.CUT_COPPER_SLAB));
        register(biConsumer, FakeBlocks.DOOR, waxed(Blocks.COPPER_DOOR));
        register(biConsumer, FakeBlocks.TRAPDOOR, waxed(Blocks.COPPER_TRAPDOOR));
        register(biConsumer, FakeBlocks.CHAIN, waxed(Blocks.COPPER_CHAIN));
        register(biConsumer, FakeBlocks.LANTERN, waxed(Blocks.COPPER_LANTERN));
        register(biConsumer, FakeBlocks.BARS, waxed(Blocks.COPPER_BARS));
        register(biConsumer, FakeBlocks.LIGHTNING_ROD, waxed(Blocks.LIGHTNING_ROD));
        register(biConsumer, FakeBlocks.SIGN,
                Blocks.OAK_SIGN,
                Blocks.SPRUCE_SIGN,
                Blocks.BIRCH_SIGN,
                Blocks.ACACIA_SIGN,
                Blocks.CHERRY_SIGN,
                Blocks.JUNGLE_SIGN,
                Blocks.DARK_OAK_SIGN,
                Blocks.PALE_OAK_SIGN,
                Blocks.MANGROVE_SIGN,
                Blocks.BAMBOO_SIGN
        );
        register(biConsumer, FakeBlocks.WALL_SIGN,
                Blocks.OAK_WALL_SIGN,
                Blocks.SPRUCE_WALL_SIGN,
                Blocks.BIRCH_WALL_SIGN,
                Blocks.ACACIA_WALL_SIGN,
                Blocks.CHERRY_WALL_SIGN,
                Blocks.JUNGLE_WALL_SIGN,
                Blocks.DARK_OAK_WALL_SIGN,
                Blocks.PALE_OAK_WALL_SIGN,
                Blocks.MANGROVE_WALL_SIGN,
                Blocks.BAMBOO_WALL_SIGN
        );
        register(biConsumer, FakeBlocks.HANGING_SIGN,
                Blocks.OAK_HANGING_SIGN,
                Blocks.SPRUCE_HANGING_SIGN,
                Blocks.BIRCH_HANGING_SIGN,
                Blocks.ACACIA_HANGING_SIGN,
                Blocks.CHERRY_HANGING_SIGN,
                Blocks.JUNGLE_HANGING_SIGN,
                Blocks.DARK_OAK_HANGING_SIGN,
                Blocks.PALE_OAK_HANGING_SIGN,
                Blocks.MANGROVE_HANGING_SIGN,
                Blocks.BAMBOO_HANGING_SIGN
        );
        register(biConsumer, FakeBlocks.WALL_HANGING_SIGN,
                Blocks.OAK_WALL_HANGING_SIGN,
                Blocks.SPRUCE_WALL_HANGING_SIGN,
                Blocks.BIRCH_WALL_HANGING_SIGN,
                Blocks.ACACIA_WALL_HANGING_SIGN,
                Blocks.CHERRY_WALL_HANGING_SIGN,
                Blocks.JUNGLE_WALL_HANGING_SIGN,
                Blocks.DARK_OAK_WALL_HANGING_SIGN,
                Blocks.PALE_OAK_WALL_HANGING_SIGN,
                Blocks.MANGROVE_WALL_HANGING_SIGN,
                Blocks.BAMBOO_WALL_HANGING_SIGN
        );

        registerRawPrefixed(
                biConsumer,
                FakeBlocks.STRAIGHT_RAIL,
                maglev("powered_maglev_rail"),
                maglev("variable_maglev_rail")
        );
        registerRawPrefixed(
                biConsumer,
                FakeBlocks.CURVED_RAIL,
                maglev("maglev_rail")
        );

        registerRawPrefixed(
                biConsumer,
                FakeBlocks.COPPERIER_PRESSURE_PLATE,
                copperierAge("weighted_pressure_plate")
        );
        registerRawPrefixed(
                biConsumer,
                FakeBlocks.BUTTON,
                copperierAge("copper_button")
        );
    }

    @Override
    public String getName() {
        return "Overlay Models";
    }
}
