package archives.tater.waxyvision.mixin.modelloading;

import archives.tater.waxyvision.WaxyVision;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.resources.model.BlockStateDefinitions;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.HashMap;

@Mixin(BlockStateDefinitions.class)
public class BlockStateDefinitionsMixin {
    @ModifyExpressionValue(
            method = "definitionLocationToBlockStateMapper",
            at = @At(value = "NEW", target = "(Ljava/util/Map;)Ljava/util/HashMap;")
    )
    private static HashMap<Identifier, StateDefinition<Block, BlockState>> addOverlayModels(HashMap<Identifier, StateDefinition<Block, BlockState>> original) {
        WaxyVision.overlayModels.getEntries().join().forEach(entry -> original.put(entry.id(), entry.definition()));
        return original;
    }
}
