package archives.tater.waxyvision.mixin.datagen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;

@Mixin(BlockModelGenerators.class)
public interface BlockModelGeneratorsAccessor {
    @Accessor
    static PropertyDispatch<VariantMutator> getROTATIONS_COLUMN_WITH_FACING() {
        throw new UnsupportedOperationException();
    }
}
