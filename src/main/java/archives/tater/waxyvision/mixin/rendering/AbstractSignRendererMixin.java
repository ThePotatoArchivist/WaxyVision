package archives.tater.waxyvision.mixin.rendering;

import archives.tater.waxyvision.WaxyVision;
import archives.tater.waxyvision.WaxyVisionBuiltinTextures;
import archives.tater.waxyvision.WaxyVisionCommon;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

@Mixin(AbstractSignRenderer.class)
public class AbstractSignRendererMixin<S extends SignRenderState> {
    @Unique
    private static final ThreadLocal<Boolean> WAXED = ThreadLocal.withInitial(() -> false);

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/level/block/entity/SignBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/SignRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            at = @At("TAIL")
    )
    private void extractWaxed(SignBlockEntity blockEntity, SignRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress, CallbackInfo ci) {
        state.setData(WaxyVision.WAXED, WaxyVision.showOverlay && blockEntity.hasAttached(WaxyVisionCommon.WAXED));
    }

    @Inject(
            method = "submitSignWithText",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/AbstractSignRenderer;submitSign(Lcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/level/block/state/properties/WoodType;Lnet/minecraft/client/model/Model$Simple;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V")
    )
    private void saveState(S state, PoseStack poseStack, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, SubmitNodeCollector submitNodeCollector, CallbackInfo ci) {
        WAXED.set(state.getDataOrDefault(WaxyVision.WAXED, false));
    }

    @SuppressWarnings({"DataFlowIssue", "ConstantValue"})
    @WrapOperation(
            method = "submitSign",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;IIILnet/minecraft/client/resources/model/sprite/SpriteId;Lnet/minecraft/client/resources/model/sprite/SpriteGetter;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V")
    )
    private <T> void renderWaxed(SubmitNodeCollector instance, Model<T> model, T state, PoseStack poseStack, int lightCoords, int overlayCoords, int tintedColor, SpriteId sprite, SpriteGetter sprites, int outlineColor, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, Operation<Void> original) {
        original.call(instance, model, state, poseStack, lightCoords, overlayCoords, tintedColor, sprite, sprites, outlineColor, crumblingOverlay);
        if (!WAXED.get()) return;
        var overlaySprite = switch ((Object) this) {
            case StandingSignRenderer ignored -> WaxyVisionBuiltinTextures.SIGN_OVERLAY;
            case HangingSignRenderer ignored -> WaxyVisionBuiltinTextures.HANGING_SIGN_OVERLAY;
            default -> null;
        };
        if (overlaySprite == null) return;
        instance.order(1).submitModel(model, state, poseStack, lightCoords, overlayCoords, tintedColor, overlaySprite, sprites, outlineColor, crumblingOverlay);
    }
}
