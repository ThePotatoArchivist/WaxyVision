package archives.tater.waxyvision.mixin.rendering;

import archives.tater.waxyvision.WaxyVision;
import archives.tater.waxyvision.WaxyVisionBuiltinTextures;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

@Mixin(ChestRenderer.class)
public class ChestRendererMixin {
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            at = @At("TAIL")
    )
    private <T extends BlockEntity & LidBlockEntity> void extractWaxed(T blockEntity, ChestRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, CallbackInfo ci) {
        state.setData(WaxyVision.WAXED, WaxyVision.showOverlay && !(blockEntity.getBlockState().getBlock() instanceof WeatheringCopper));
    }

    @WrapOperation(
            method = "submit(Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;IIILnet/minecraft/client/resources/model/sprite/SpriteId;Lnet/minecraft/client/resources/model/sprite/SpriteGetter;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V")
    )
    private <T> void renderWaxed(SubmitNodeCollector instance, Model<T> model, T state, PoseStack poseStack, int lightCoords, int overlayCoords, int tintedColor, SpriteId sprite, SpriteGetter sprites, int outlineColor, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, Operation<Void> original, ChestRenderState chestRenderState) {
        original.call(instance, model, state, poseStack, lightCoords, overlayCoords, tintedColor, sprite, sprites, outlineColor, crumblingOverlay);
        if (!chestRenderState.getDataOrDefault(WaxyVision.WAXED, false)) return;
        instance.order(1).submitModel(model, state, poseStack, lightCoords, overlayCoords, tintedColor, WaxyVisionBuiltinTextures.getChestOverlaySprite(chestRenderState.type), sprites, outlineColor, crumblingOverlay);
    }
}
