package archives.tater.waxyvision.mixin.rendering;

import archives.tater.waxyvision.WaxyVision;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.CopperGolemStatueBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.CopperGolemStatueRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.world.phys.Vec3;

@Mixin(CopperGolemStatueBlockRenderer.class)
public class CopperGolemStatueBlockRendererMixin {
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/level/block/entity/CopperGolemStatueBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/CopperGolemStatueRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            at = @At("TAIL")
    )
    private void extractWaxed(CopperGolemStatueBlockEntity blockEntity, CopperGolemStatueRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress, CallbackInfo ci) {
        state.setData(WaxyVision.WAXED, WaxyVision.showOverlay && !(blockEntity.getBlockState().getBlock() instanceof WeatheringCopper));
    }

    @WrapOperation(
            method = "submit(Lnet/minecraft/client/renderer/blockentity/state/CopperGolemStatueRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/resources/Identifier;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V")
    )
    private <S> void renderOverlay(SubmitNodeCollector instance, Model<? super S> model, S state, PoseStack poseStack, Identifier texture, int packedLight, int packedOverlay, int outlineColor, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, Operation<Void> original, CopperGolemStatueRenderState statueRenderState) {
        original.call(instance, model, state, poseStack, texture, packedLight, packedOverlay, outlineColor, crumblingOverlay);
        if (!statueRenderState.getDataOrDefault(WaxyVision.WAXED, false)) return;
        instance.order(1).submitModel(model, state, poseStack, WaxyVision.COPPER_GOLEM_OVERLAY, packedLight, packedOverlay, outlineColor, crumblingOverlay);
    }
}
