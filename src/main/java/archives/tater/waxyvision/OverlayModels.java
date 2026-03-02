package archives.tater.waxyvision;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

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
    private final List<Entry> entries = new ArrayList<>();
    private final Map<Block, Entry> overlays = new IdentityHashMap<>();

    @Override
    public CompletableFuture<Void> reload(SharedState sharedState, Executor exectutor, PreparationBarrier barrier, Executor applyExectutor) {
        overlays.clear();
        entries.clear();
        return CompletableFuture.runAsync(() -> sharedState.resourceManager().listResources(PATH, id -> true).entrySet()
                .stream().flatMap(entry -> {
                    var resource = entry.getValue();
                    try (var reader = resource.openAsReader(); var jsonReader = new JsonReader(reader)) {
                        return UnbakedEntry.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(jsonReader))
                                .resultOrPartial()
                                .stream();
                    } catch (IOException e) {
                        WaxyVision.LOGGER.error("Failed to read", e);
                        return Stream.of();
                    }
                })
                .forEach(unbakedEntry -> {
                    var builder = new StateDefinition.Builder<Block, BlockState>(Blocks.AIR);
                    var properties = unbakedEntry.blocks.stream().flatMap(block -> block.getStateDefinition().getProperties().stream()).collect(Collectors.toSet());
                    for (var property : properties)
                        builder.add(property);
                    var definition = builder.create(Block::defaultBlockState, BlockState::new);
                    var entry = new Entry(unbakedEntry.model, definition);
                    entries.add(entry);
                    for (var block : unbakedEntry.blocks)
                        overlays.put(block, entry);
                })
        ).thenCompose(barrier::wait);
    }

    public @Nullable Entry getEntry(Block block) {
        return overlays.get(block);
    }

    public Collection<Entry> getEntries() {
        return entries;
    }

    public record UnbakedEntry(Identifier model, List<Block> blocks) {
        public static final Codec<UnbakedEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("model").forGetter(UnbakedEntry::model),
                BuiltInRegistries.BLOCK.byNameCodec().listOf().fieldOf("blocks").forGetter(UnbakedEntry::blocks)
        ).apply(instance, UnbakedEntry::new));
    }

    public record Entry(Identifier id, StateDefinition<Block, BlockState> definition) {}
}
