package archives.tater.waxyvision.mixin;

import archives.tater.waxyvision.WaxyVision;
import archives.tater.waxyvision.WaxyVisionCommon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.entity.CopperGolemRenderer;
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import net.minecraft.world.entity.animal.golem.CopperGolem;

@SuppressWarnings("UnstableApiUsage")
@Mixin(CopperGolemRenderer.class)
public class CopperGolemRendererMixin {
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/animal/golem/CopperGolem;Lnet/minecraft/client/renderer/entity/state/CopperGolemRenderState;F)V",
            at = @At("TAIL")
    )
    private void extractWaxed(CopperGolem copperGolem, CopperGolemRenderState copperGolemRenderState, float f, CallbackInfo ci) {
        copperGolemRenderState.setData(WaxyVision.WAXED, WaxyVision.showOverlay && copperGolem.hasAttached(WaxyVisionCommon.WAXED));
    }
}
