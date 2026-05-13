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
import net.minecraft.world.level.block.WeatheringCopperBlocks;

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

    private static List<Block> waxed(WeatheringCopperBlocks blocks) {
        return List.of(
                blocks.waxed(),
                blocks.waxedExposed(),
                blocks.waxedWeathered(),
                blocks.waxedOxidized()
        );
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

    private static void register(BiConsumer<Identifier, OverlayModels.UnbakedEntry> consumer, Block modelBlock, List<Block> blocks) {
        registerRaw(consumer, modelBlock, getIds(blocks.stream()).toList());
    }

    private static void register(BiConsumer<Identifier, OverlayModels.UnbakedEntry> consumer, Block modelBlock, Block... blocks) {
        registerRaw(consumer, modelBlock, getIds(Arrays.stream(blocks)).toList());
    }

    private static void registerRawPrefixed(BiConsumer<Identifier, OverlayModels.UnbakedEntry> consumer, Block modelBlock, Identifier... blocks) {
            registerRaw(consumer, modelBlock, prefixed(blocks).toList());
    }

    private static Identifier maglev(String path) {
        return Identifier.fromNamespaceAndPath("maglev", path);
    }

    private static Identifier copperierAge(String path) {
        return Identifier.fromNamespaceAndPath("thecopperierage", path);
    }

    @Override
    protected void configure(BiConsumer<Identifier, OverlayModels.UnbakedEntry> biConsumer, HolderLookup.Provider provider) {
        register(biConsumer, FakeBlocks.CUBE,
                Blocks.WAXED_COPPER_BLOCK,
                Blocks.WAXED_CUT_COPPER,
                Blocks.WAXED_CHISELED_COPPER,
                Blocks.WAXED_COPPER_GRATE,
                Blocks.WAXED_COPPER_BULB,
                Blocks.WAXED_EXPOSED_COPPER,
                Blocks.WAXED_EXPOSED_CUT_COPPER,
                Blocks.WAXED_EXPOSED_CHISELED_COPPER,
                Blocks.WAXED_EXPOSED_COPPER_GRATE,
                Blocks.WAXED_EXPOSED_COPPER_BULB,
                Blocks.WAXED_WEATHERED_COPPER,
                Blocks.WAXED_WEATHERED_CUT_COPPER,
                Blocks.WAXED_WEATHERED_CHISELED_COPPER,
                Blocks.WAXED_WEATHERED_COPPER_GRATE,
                Blocks.WAXED_WEATHERED_COPPER_BULB,
                Blocks.WAXED_OXIDIZED_COPPER,
                Blocks.WAXED_OXIDIZED_CUT_COPPER,
                Blocks.WAXED_OXIDIZED_CHISELED_COPPER,
                Blocks.WAXED_OXIDIZED_COPPER_GRATE,
                Blocks.WAXED_OXIDIZED_COPPER_BULB
        );
        register(biConsumer, FakeBlocks.STAIRS,
                Blocks.WAXED_CUT_COPPER_STAIRS,
                Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS,
                Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS,
                Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS
        );
        register(biConsumer, FakeBlocks.SLAB,
                Blocks.WAXED_CUT_COPPER_SLAB,
                Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB,
                Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB,
                Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB
        );
        register(biConsumer, FakeBlocks.DOOR,
                Blocks.WAXED_COPPER_DOOR,
                Blocks.WAXED_EXPOSED_COPPER_DOOR,
                Blocks.WAXED_WEATHERED_COPPER_DOOR,
                Blocks.WAXED_OXIDIZED_COPPER_DOOR
        );
        register(biConsumer, FakeBlocks.TRAPDOOR,
                Blocks.WAXED_COPPER_TRAPDOOR,
                Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR,
                Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR,
                Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR
        );
        register(biConsumer, FakeBlocks.CHAIN, waxed(Blocks.COPPER_CHAIN));
        register(biConsumer, FakeBlocks.LANTERN, waxed(Blocks.COPPER_LANTERN));
        register(biConsumer, FakeBlocks.BARS, waxed(Blocks.COPPER_BARS));
        register(biConsumer, FakeBlocks.LIGHTNING_ROD,
                Blocks.WAXED_LIGHTNING_ROD,
                Blocks.WAXED_EXPOSED_LIGHTNING_ROD,
                Blocks.WAXED_WEATHERED_LIGHTNING_ROD,
                Blocks.WAXED_OXIDIZED_LIGHTNING_ROD
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
