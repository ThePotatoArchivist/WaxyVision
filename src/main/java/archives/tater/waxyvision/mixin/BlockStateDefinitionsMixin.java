package archives.tater.waxyvision.mixin;

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
        WaxyVision.overlayModels.getEntries().forEach(entry -> original.put(entry.id(), entry.definition()));
        return original;
    }

//    @WrapWithCondition(
//            method = "definitionLocationToBlockStateMapper",
//            at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")
//    )
//    private static <K, V> boolean prevent(Map<K, V> instance, K k, V v, @Local Block block) {
//        return WaxyVision.overlayModels.getEntry(block) != null;
//    }

//    @Inject(
//            method = "definitionLocationToBlockStateMapper",
//            at = @At(value = "FIELD", target = "Lnet/minecraft/core/registries/BuiltInRegistries;BLOCK:Lnet/minecraft/core/DefaultedRegistry;"),
//            cancellable = true
//    )
//    private static void earlyReturnNOCOMMIT(CallbackInfoReturnable<Function<Identifier, StateDefinition<Block, BlockState>>> cir, @Local Map<Identifier, StateDefinition<Block, BlockState>> map) {
//        cir.setReturnValue(map::get);
//    }
}
