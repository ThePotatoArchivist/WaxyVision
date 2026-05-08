package archives.tater.waxyvision;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;

public class WaxyVisionConfig extends ReflectiveConfig {
    @Comment("Enable/disable the mod. Can also be controlled with a keybinding.")
    public final TrackedValue<Boolean> enabled = value(true);

    @Comment("Whether the waxed overlay should only display when the right item is held")
    public final TrackedValue<Boolean> requireItem = value(true);

    @Comment("Items that trigger the waxed overlay")
    public final TrackedValue<ValueList<String>> waxItems = list("", "minecraft:honeycomb");
}
