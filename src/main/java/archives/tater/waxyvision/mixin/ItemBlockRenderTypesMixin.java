package archives.tater.waxyvision.mixin;

import archives.tater.waxyvision.WaxyVision;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ItemBlockRenderTypes.class)
public class ItemBlockRenderTypesMixin {
    @ModifyReturnValue(
            method = "getChunkRenderType",
            at = @At("RETURN")
    )
    private static ChunkSectionLayer modifyRenderType(ChunkSectionLayer original, BlockState state) {
        return WaxyVision.showOverlay && WaxyVision.overlayModels.getEntry(state.getBlock()) != null && original == ChunkSectionLayer.SOLID
                ? ChunkSectionLayer.CUTOUT
                : original;
    }

    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(
            method = "getMovingBlockRenderType",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;")
    )
    private static <V> V modifyRenderType(V original, BlockState state) {
        return WaxyVision.showOverlay && WaxyVision.overlayModels.getEntry(state.getBlock()) != null && original == ChunkSectionLayer.SOLID
                ? (V) ChunkSectionLayer.CUTOUT
                : original;
    }
}
