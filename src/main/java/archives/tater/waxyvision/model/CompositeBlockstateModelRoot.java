package archives.tater.waxyvision.model;

import net.fabricmc.fabric.api.client.model.loading.v1.CompositeBlockStateModel;

import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CompositeBlockstateModelRoot implements BlockStateModel.UnbakedRoot {
    private final List<BlockStateModel.UnbakedRoot> children;

    public CompositeBlockstateModelRoot(List<BlockStateModel.UnbakedRoot> children) {
        this.children = List.copyOf(children);
    }

    @Override
    public BlockStateModel bake(BlockState state, ModelBaker baker) {
        return CompositeBlockStateModel.of(children.stream().map(root -> root.bake(state, baker)).toList());
    }

    @Override
    public Object visualEqualityGroup(BlockState state) {
        return children.stream().map(root -> root.visualEqualityGroup(state)).toList();
    }

    @Override
    public void resolveDependencies(Resolver resolver) {
        children.forEach(root -> root.resolveDependencies(resolver));
    }
}
