package archives.tater.waxyvision;

import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class OverlayBlockStateModel extends WrapperBlockStateModel {
    public OverlayBlockStateModel(BlockStateModel wrapped) {
        super(wrapped);
    }

    private boolean shouldShow() {
        return WaxyVision.showOverlay;
    }

    @Override
    public void collectParts(RandomSource random, List<BlockModelPart> parts) {
        if (shouldShow())
            super.collectParts(random, parts);
    }

    @Override
    public List<BlockModelPart> collectParts(RandomSource random) {
        return shouldShow() ? super.collectParts(random) : List.of();
    }

    @Override
    public void emitQuads(QuadEmitter emitter, BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
        if (shouldShow())
            super.emitQuads(emitter, blockView, pos, state, random, cullTest);
    }

    @Override
    public @Nullable Object createGeometryKey(BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random) {
        if (!shouldShow()) return null;
        var child = super.createGeometryKey(blockView, pos, state, random);
        return child == null ? null : new GeometryKey(child);
    }

    public record Unbaked(BlockStateModel.Unbaked wrapped) implements BlockStateModel.Unbaked {
        @Override
        public BlockStateModel bake(ModelBaker baker) {
            return new OverlayBlockStateModel(wrapped.bake(baker));
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            wrapped.resolveDependencies(resolver);
        }
    }

    private record GeometryKey(Object child) {}
}
