package archives.tater.waxyvision;

import archives.tater.waxyvision.datagen.FakeBlocks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import net.minecraft.util.Unit;

public class WaxyVisionCommon implements ModInitializer {

    public static final AttachmentType<Unit> WAXED = AttachmentRegistry.create(WaxyVision.id("waxed"), builder -> builder
            .syncWith(Unit.STREAM_CODEC, AttachmentSyncPredicate.all())
    );

    public static void setWaxed(AttachmentTarget target, boolean waxed) {
        if (waxed)
            target.setAttached(WAXED, Unit.INSTANCE);
        else
            target.removeAttached(WAXED);
    }

    @Override
    public void onInitialize() {
        if (System.getProperty("fabric-api.datagen") != null)
            FakeBlocks.init();
    }
}
