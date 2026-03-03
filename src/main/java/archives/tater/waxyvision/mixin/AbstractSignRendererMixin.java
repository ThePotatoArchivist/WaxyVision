package archives.tater.waxyvision.mixin;

import archives.tater.waxyvision.WaxyVision;
import archives.tater.waxyvision.WaxyVisionCommon;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
@Mixin(AbstractSignRenderer.class)
public class AbstractSignRendererMixin {
    @Shadow
    @Final
    private MaterialSet materials;
    @Unique
    private static final ThreadLocal<Boolean> WAXED = ThreadLocal.withInitial(() -> false);

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/level/block/entity/SignBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/SignRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            at = @At("TAIL")
    )
    private void extractWaxed(SignBlockEntity signBlockEntity, SignRenderState signRenderState, float f, Vec3 vec3, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, CallbackInfo ci) {
        signRenderState.setData(WaxyVision.WAXED, WaxyVision.showOverlay && signBlockEntity.hasAttached(WaxyVisionCommon.WAXED));
    }

    @Inject(
            method = "submitSignWithText",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/AbstractSignRenderer;submitSign(Lcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/level/block/state/properties/WoodType;Lnet/minecraft/client/model/Model$Simple;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V")
    )
    private void saveState(SignRenderState renderState, PoseStack poseStack, BlockState blockState, SignBlock sign, WoodType woodType, Model.Simple model, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, SubmitNodeCollector nodeCollector, CallbackInfo ci) {
        WAXED.set(renderState.getDataOrDefault(WaxyVision.WAXED, false));
    }

    @SuppressWarnings({"DataFlowIssue", "ConstantValue"})
    @WrapOperation(
            method = "submitSign",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V")
    )
    private <S> void renderWaxed(SubmitNodeCollector instance, Model<? super S> model, S renderState, PoseStack poseStack, RenderType renderType, int packedLight, int packedOverlay, int tintColor, @Nullable TextureAtlasSprite sprite, int outlineColor, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, Operation<Void> original) {
        original.call(instance, model, renderState, poseStack, renderType, packedLight, packedOverlay, tintColor, sprite, outlineColor, crumblingOverlay);
        if (!WAXED.get()) return;
        var material = switch ((Object) this) {
            case SignRenderer ignored -> WaxyVision.SIGN_OVERLAY;
            case HangingSignRenderer ignored -> WaxyVision.HANGING_SIGN_OVERLAY;
            default -> null;
        };
        if (material == null) return;
        instance.order(1).submitModel(model, renderState, poseStack, renderType, packedLight, packedOverlay, tintColor, materials.get(material), outlineColor, crumblingOverlay);
    }
}
