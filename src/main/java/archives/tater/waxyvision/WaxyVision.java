package archives.tater.waxyvision;

import archives.tater.waxyvision.mixin.LevelRendererAccessor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.SingleVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Items;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class WaxyVision implements ClientModInitializer {
	public static final String MOD_ID = "waxyvision";
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static boolean showOverlay = false;

	public static final Identifier OUTLINE_CUBE = id("block/outline_cube");
	public static final ExtraModelKey<BlockStateModel> OUTLINE_CUBE_KEY = ExtraModelKey.create();

	@Override
	public void onInitializeClient() {
		ModelLoadingPlugin.register(pluginContext -> {
			pluginContext.modifyBlockModelOnLoad().register((model, context) -> {
				var state = context.state();
				if (!HoneycombItem.WAXABLES.get().containsValue(state.getBlock())) return model;

				return new CompositeBlockstateModelRoot(List.of(
						model,
						new BlockStateModel.SimpleCachedUnbakedRoot(new OverlayBlockStateModel.Unbaked(new SingleVariant.Unbaked(new Variant(OUTLINE_CUBE))))
				));
			});
		});

		ClientTickEvents.START_WORLD_TICK.register(clientLevel -> {
			var player = Minecraft.getInstance().player;
			if (player == null) return;

			var newShowOverlay = Arrays.stream(InteractionHand.values())
					.anyMatch(hand -> player.getItemInHand(hand).is(Items.HONEYCOMB));
			if (showOverlay != newShowOverlay) {
				showOverlay = newShowOverlay;
				for (var section : ((LevelRendererAccessor) Minecraft.getInstance().levelRenderer).getViewArea().sections)
                    section.setDirty(false);
			}
		});
	}
}