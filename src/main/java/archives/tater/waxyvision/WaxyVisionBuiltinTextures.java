package archives.tater.waxyvision;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.ChestType;

public class WaxyVisionBuiltinTextures {
    private WaxyVisionBuiltinTextures() {}

    private static SpriteId sprite(Identifier sheet, String path) {
        return new SpriteId(sheet, WaxyVision.id(path));
    }

    public static final Identifier COPPER_GOLEM_OVERLAY = WaxyVision.id("textures/entity/copper_golem.png");
    public static final SpriteId SIGN_OVERLAY = sprite(Sheets.SIGN_SHEET, "entity/signs/sign");
    public static final SpriteId HANGING_SIGN_OVERLAY = sprite(Sheets.SIGN_SHEET, "entity/signs/hanging_sign");
    public static final SpriteId CHEST_OVERLAY_SINGLE = sprite(Sheets.CHEST_SHEET, "entity/chest/single");
    public static final SpriteId CHEST_OVERLAY_RIGHT = sprite(Sheets.CHEST_SHEET, "entity/chest/left");
    public static final SpriteId CHEST_OVERLAY_LEFT = sprite(Sheets.CHEST_SHEET, "entity/chest/right");

    public static SpriteId getChestOverlaySprite(ChestType type) {
        return switch (type) {
            case SINGLE -> CHEST_OVERLAY_SINGLE;
            case LEFT -> CHEST_OVERLAY_LEFT;
            case RIGHT -> CHEST_OVERLAY_RIGHT;
        };
    }
}
