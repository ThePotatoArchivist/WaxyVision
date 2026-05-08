package archives.tater.waxyvision;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;

public class WaxyVisionKeys {
    public static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(WaxyVision.MOD_ID, "category")
    );

    public static final KeyMapping REQUIRE_ITEM_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key." + WaxyVision.MOD_ID + ".toggle_item_required",
            InputConstants.Type.KEYSYM,
            -1,
            KEY_CATEGORY
    ));

    public static final KeyMapping ENABLE_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key." + WaxyVision.MOD_ID + ".toggle_enabled",
            InputConstants.Type.KEYSYM,
            -1,
            KEY_CATEGORY
    ));

    public static final String ENABLED_TRANSLATION = WaxyVision.MOD_ID + ".toggle.enabled";
    public static final String REQUIRE_TRANSLATION = WaxyVision.MOD_ID + ".toggle.requireItem";
    public static final String ON_TRANSLATION = WaxyVision.MOD_ID + ".toggle.on";
    public static final String OFF_TRANSLATION = WaxyVision.MOD_ID + ".toggle.off";
    public static final Component ON_TEXT = Component.translatable(ON_TRANSLATION)
            .withStyle(ChatFormatting.GREEN);
    public static final Component OFF_TEXT = Component.translatable(OFF_TRANSLATION)
            .withStyle(ChatFormatting.RED);

    private static void toggle(Minecraft client, TrackedValue<Boolean> tracked, String translationKey) {
        var newValue = !tracked.value();
        tracked.setValue(newValue);
        if (client.player == null) return;
        client.player.sendOverlayMessage(Component.translatable(translationKey, newValue ? ON_TEXT : OFF_TEXT));
    }

    static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (ENABLE_KEY.consumeClick())
                toggle(client, WaxyVision.CONFIG.enabled, ENABLED_TRANSLATION);

            while (REQUIRE_ITEM_KEY.consumeClick())
                toggle(client, WaxyVision.CONFIG.requireItem, REQUIRE_TRANSLATION);
        });
    }
}
