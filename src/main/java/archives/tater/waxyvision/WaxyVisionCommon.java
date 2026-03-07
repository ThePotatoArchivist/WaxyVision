package archives.tater.waxyvision;

import archives.tater.waxyvision.datagen.FakeBlocks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Unit;

@SuppressWarnings("UnstableApiUsage")
public class WaxyVisionCommon implements ModInitializer {

    public static final CustomPacketPayload.Type<CustomPacketPayload> DUMMY = new CustomPacketPayload.Type<>(WaxyVision.id("dummy"));
    private static final CustomPacketPayload DUMMY_INSTANCE = () -> DUMMY;

    public static final AttachmentSyncPredicate WAXYVISION_INSTALLED = (attachmentTarget, serverPlayer) ->
            ServerPlayNetworking.canSend(serverPlayer, DUMMY);

    public static final AttachmentType<Unit> WAXED = AttachmentRegistry.create(WaxyVision.id("waxed"), builder -> builder
            .syncWith(Unit.STREAM_CODEC, WAXYVISION_INSTALLED)
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

        PayloadTypeRegistry.playS2C().register(DUMMY, StreamCodec.unit(DUMMY_INSTANCE));
    }
}
