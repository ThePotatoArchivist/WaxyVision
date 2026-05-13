package archives.tater.waxyvision;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.Shapes;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OverlayModels implements PreparableReloadListener {
    public static final String PATH = "waxyvision/overlays";
    public static final Identifier CUBE_MODEL = WaxyVision.id("cube");

    private CompletableFuture<List<Entry>> entries = CompletableFuture.completedFuture(List.of());
    private final Map<Block, Entry> overlays = new IdentityHashMap<>();

    @Override
    public CompletableFuture<Void> reload(SharedState currentReload, Executor taskExecutor, PreparationBarrier preparationBarrier, Executor reloadExecutor) {
        overlays.clear();
        entries = CompletableFuture.supplyAsync(() -> currentReload.resourceManager().listResources(PATH, _ -> true).entrySet().stream()
                .flatMap(entry -> {
                    var resource = entry.getValue();
                    try (var reader = resource.openAsReader(); var jsonReader = new JsonReader(reader)) {
                        var result = UnbakedEntry.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(jsonReader));
                        result.ifError(error ->
                            WaxyVision.LOGGER.error("Failed to read overlay model {}: {}", entry.getKey(), error.message())
                        );
                        return result.resultOrPartial().stream();
                    } catch (IOException e) {
                        WaxyVision.LOGGER.error("Failed to read", e);
                        return Stream.of();
                    }
                })
                .map(unbakedEntry -> {
                    var builder = new StateDefinition.Builder<Block, BlockState>(Blocks.AIR);
                    var blocks = unbakedEntry.blocks.stream()
                            .flatMap(block -> BuiltInRegistries.BLOCK.get(block).stream())
                            .map(Holder.Reference::value)
                            .toList();
                    if (blocks.isEmpty()) return null;
                    var properties = blocks.stream().flatMap(block -> block.getStateDefinition().getProperties().stream()).collect(Collectors.toSet());
                    try {
                        for (var property : properties)
                            builder.add(property);
                    } catch (IllegalArgumentException e) {
                        WaxyVision.LOGGER.error("Incompatible blockstates for overlay {}: {}", unbakedEntry.model, e.getMessage());
                        return null;
                    }
                    var definition = builder.create(Block::defaultBlockState, BlockState::new);
                    var entry = new Entry(unbakedEntry.model, definition);
                    for (var block : blocks)
                        overlays.put(block, entry);

                    if (entry.id.equals(CUBE_MODEL))
                        for (var block : HoneycombItem.WAXABLES.get().values())
                            if (block.defaultBlockState().getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).equals(Shapes.block()))
                                overlays.putIfAbsent(block, entry);

                    return entry;
                })
                .filter(Objects::nonNull)
                .toList(),
        taskExecutor);

        return entries.thenCompose(preparationBarrier::wait).thenAccept(_ -> {});
    }

    public @Nullable Entry getEntry(Block block) {
        return overlays.get(block);
    }

    public CompletableFuture<? extends Collection<Entry>> getEntries() {
        return entries;
    }

    public record UnbakedEntry(Identifier model, List<Identifier> blocks) {
        public static final Codec<UnbakedEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("model").forGetter(UnbakedEntry::model),
                Identifier.CODEC.listOf().fieldOf("blocks").forGetter(UnbakedEntry::blocks)
        ).apply(instance, UnbakedEntry::new));
    }

    public record Entry(Identifier id, StateDefinition<Block, BlockState> definition) {}
}
