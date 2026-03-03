package archives.tater.waxyvision;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.animal.golem.CopperGolemModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class CopperGolemWaxLayer extends RenderLayer<CopperGolemRenderState, CopperGolemModel> {

    private static final RenderType RENDER_TYPE = RenderTypes.entityCutoutNoCull(WaxyVision.COPPER_GOLEM_OVERLAY);

    public CopperGolemWaxLayer(RenderLayerParent<CopperGolemRenderState, CopperGolemModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, CopperGolemRenderState renderState, float yRot, float xRot) {
        if (!WaxyVision.showOverlay || !renderState.getDataOrDefault(WaxyVision.WAXED, false)) return;
        nodeCollector.order(1).submitModel(
                getParentModel(),
                renderState,
                poseStack,
                RENDER_TYPE,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                renderState.outlineColor,
                null
        );
    }
}
