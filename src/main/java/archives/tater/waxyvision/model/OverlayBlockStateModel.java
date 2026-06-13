package archives.tater.waxyvision.model;

import archives.tater.waxyvision.WaxyVision;
import archives.tater.waxyvision.WaxyVisionCommon;

import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.SignBlockEntity;
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

    private boolean shouldShow(BlockAndTintGetter blockView, BlockPos pos, BlockState state) {
        return shouldShow() && (!state.hasBlockEntity() || !(blockView.getBlockEntity(pos) instanceof SignBlockEntity signBlockEntity) || signBlockEntity.hasAttached(WaxyVisionCommon.WAXED));
    }

    @Override
    public void collectParts(RandomSource random, List<BlockStateModelPart> parts) {
        if (shouldShow())
            super.collectParts(random, parts);
    }

    @Override
    public void emitQuads(QuadEmitter emitter, BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
        if (shouldShow(blockView, pos, state))
            super.emitQuads(emitter, blockView, pos, state, random, cullTest);
    }

    @Override
    public @Nullable Object createGeometryKey(BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random) {
        var geometryKey = super.createGeometryKey(blockView, pos, state, random);
        if (geometryKey == null) return null;
        return new GeometryKey(geometryKey);
    }

    @Override
    public Material.Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        return super.particleMaterial(level, pos, state);
    }

    public record Unbaked(BlockStateModel.UnbakedRoot wrapped) implements BlockStateModel.UnbakedRoot {
        @Override
        public BlockStateModel bake(BlockState state, ModelBaker baker) {
            return new OverlayBlockStateModel(wrapped.bake(state, baker));
        }

        @Override
        public Object visualEqualityGroup(BlockState state) {
            return new GeometryKey(wrapped.visualEqualityGroup(state));
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            wrapped.resolveDependencies(resolver);
        }
    }

    private record GeometryKey(Object child) {}
}
