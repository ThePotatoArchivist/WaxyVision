package archives.tater.waxyvision.mixin.modelloading;

import archives.tater.waxyvision.WaxyVision;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(value = ModelManager.class, priority = 1500)
public class ModelManagerMixin {

    @WrapOperation(
            method = "reload",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BlockStateModelLoader;loadBlockStates(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;")
    )
    private CompletableFuture<BlockStateModelLoader.LoadedModels> saveModels(ResourceManager resourceManager, Executor executor, Operation<CompletableFuture<BlockStateModelLoader.LoadedModels>> original, @Local(argsOnly = true) PreparableReloadListener.PreparationBarrier preparationBarrier) {
        return WaxyVision.overlayModels.getEntries()
                .thenCompose(_ -> original.call(resourceManager, executor))
                .thenApply(models -> {
                    WaxyVision.loadedModels = models;
                    return models;
                });
    }
}
