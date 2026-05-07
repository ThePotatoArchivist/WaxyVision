package archives.tater.waxyvision.mixin.rendering;

import archives.tater.waxyvision.WaxyVision;
import archives.tater.waxyvision.WaxyVisionCommon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.entity.CopperGolemRenderer;
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import net.minecraft.world.entity.animal.golem.CopperGolem;

@Mixin(CopperGolemRenderer.class)
public class CopperGolemRendererMixin {
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/animal/golem/CopperGolem;Lnet/minecraft/client/renderer/entity/state/CopperGolemRenderState;F)V",
            at = @At("TAIL")
    )
    private void extractWaxed(CopperGolem entity, CopperGolemRenderState state, float partialTicks, CallbackInfo ci) {
        state.setData(WaxyVision.WAXED, WaxyVision.showOverlay && entity.hasAttached(WaxyVisionCommon.WAXED));
    }
}
