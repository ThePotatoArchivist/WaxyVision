package archives.tater.waxyvision.datagen;

import archives.tater.waxyvision.OverlayModels;
import archives.tater.waxyvision.WaxyVision;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class OverlayModelGenerator extends FabricCodecDataProvider<OverlayModels.UnbakedEntry> {
    public OverlayModelGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(dataOutput, registriesFuture, PackOutput.Target.RESOURCE_PACK, OverlayModels.PATH, OverlayModels.UnbakedEntry.CODEC);
    }

    @Override
    protected void configure(BiConsumer<Identifier, OverlayModels.UnbakedEntry> biConsumer, HolderLookup.Provider provider) {
        biConsumer.accept(WaxyVision.id("cube"), new OverlayModels.UnbakedEntry(
                WaxyVision.id("cube"),
                List.of(
                        Blocks.WAXED_COPPER_BLOCK,
                        Blocks.WAXED_CUT_COPPER,
                        Blocks.WAXED_COPPER_GRATE,
                        Blocks.WAXED_COPPER_BULB,
                        Blocks.WAXED_EXPOSED_COPPER,
                        Blocks.WAXED_EXPOSED_CUT_COPPER,
                        Blocks.WAXED_EXPOSED_COPPER_GRATE,
                        Blocks.WAXED_EXPOSED_COPPER_BULB,
                        Blocks.WAXED_WEATHERED_COPPER,
                        Blocks.WAXED_WEATHERED_CUT_COPPER,
                        Blocks.WAXED_WEATHERED_COPPER_GRATE,
                        Blocks.WAXED_WEATHERED_COPPER_BULB,
                        Blocks.WAXED_OXIDIZED_COPPER,
                        Blocks.WAXED_OXIDIZED_CUT_COPPER,
                        Blocks.WAXED_OXIDIZED_COPPER_GRATE,
                        Blocks.WAXED_OXIDIZED_COPPER_BULB
                )
        ));
        biConsumer.accept(WaxyVision.id("stairs"), new OverlayModels.UnbakedEntry(
                WaxyVision.id("stairs"),
                List.of(
                        Blocks.WAXED_CUT_COPPER_STAIRS,
                        Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS,
                        Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS,
                        Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS
                )
        ));
        biConsumer.accept(WaxyVision.id("slab"), new OverlayModels.UnbakedEntry(
                WaxyVision.id("slab"),
                List.of(
                        Blocks.WAXED_CUT_COPPER_SLAB,
                        Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB,
                        Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB,
                        Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB
                )
        ));
        biConsumer.accept(WaxyVision.id("door"), new OverlayModels.UnbakedEntry(
                WaxyVision.id("door"),
                List.of(
                        Blocks.WAXED_COPPER_DOOR,
                        Blocks.WAXED_EXPOSED_COPPER_DOOR,
                        Blocks.WAXED_WEATHERED_COPPER_DOOR,
                        Blocks.WAXED_OXIDIZED_COPPER_DOOR
                )
        ));
    }

    @Override
    public String getName() {
        return "Overlay Models";
    }
}
