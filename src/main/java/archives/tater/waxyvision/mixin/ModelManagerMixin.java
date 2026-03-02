package archives.tater.waxyvision.mixin;

import archives.tater.waxyvision.WaxyVision;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelManager;

import java.util.concurrent.CompletableFuture;

@Mixin(value = ModelManager.class, priority = 1500)
public class ModelManagerMixin {

    @ModifyExpressionValue(
            method = "reload",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BlockStateModelLoader;loadBlockStates(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;")
    )
    private CompletableFuture<BlockStateModelLoader.LoadedModels> saveModels(CompletableFuture<BlockStateModelLoader.LoadedModels> original) {
        return original.thenApply(models -> {
            WaxyVision.loadedModels = models;
            return models;
        });
    }
}
