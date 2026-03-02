package archives.tater.waxyvision.mixin;

import archives.tater.waxyvision.WaxyVision;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.CopperGolemStatueBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.CopperGolemStatueRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.level.block.WeatheringCopper;

import org.jspecify.annotations.Nullable;

@Mixin(CopperGolemStatueBlockRenderer.class)
public class CopperGolemStatueBlockRendererMixin {
    @WrapOperation(
            method = "submit(Lnet/minecraft/client/renderer/blockentity/state/CopperGolemStatueRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V")
    )
    private <S> void renderOverlay(SubmitNodeCollector instance, Model<? super S> model, S renderState, PoseStack poseStack, RenderType renderType, int packedLight, int packedOverlay, int outlineColor, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, Operation<Void> original, CopperGolemStatueRenderState statueRenderState) {
        original.call(instance, model, renderState, poseStack, renderType, packedLight, packedOverlay, outlineColor, crumblingOverlay);
        if (!WaxyVision.showOverlay || statueRenderState.blockState.getBlock() instanceof WeatheringCopper) return;
        instance.order(1).submitModel(model, renderState, poseStack, RenderTypes.entityCutoutNoCull(WaxyVision.COPPER_GOLEM_OVERLAY), packedLight, packedOverlay, outlineColor, crumblingOverlay);
    }
}
