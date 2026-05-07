package archives.tater.waxyvision;

import archives.tater.waxyvision.model.CompositeBlockstateModelRoot;
import archives.tater.waxyvision.model.OverlayBlockStateModel;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.CopperGolemRenderer;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
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

	public static final Identifier COPPER_GOLEM_OVERLAY = id("textures/entity/copper_golem.png");
	public static final SpriteId SIGN_OVERLAY = new SpriteId(Sheets.SIGN_SHEET, id("entity/signs/sign"));
	public static final SpriteId HANGING_SIGN_OVERLAY = new SpriteId(Sheets.SIGN_SHEET, id("entity/signs/hanging_sign"));

	public static final Identifier OVERLAY_MODELS_KEY = id("overlay_models");
	public static final OverlayModels overlayModels = new OverlayModels();

	public static final RenderStateDataKey<Boolean> WAXED = RenderStateDataKey.create(() -> MOD_ID + ":waxed");

	@ApiStatus.Internal
	@Nullable
	public static BlockStateModelLoader.LoadedModels loadedModels;

	private static <T extends Comparable<T>> BlockState copy(BlockState target, BlockState source, Property<T> property) {
		return target.setValue(property, source.getValue(property));
	}

	private static void rerenderAllChunks(ClientLevel clientLevel) {
		var cameraSectionPos = SectionPos.of(Minecraft.getInstance().gameRenderer.getMainCamera().position());
		var viewDistance = Minecraft.getInstance().options.getEffectiveRenderDistance();

		clientLevel.setSectionRangeDirty(
				clientLevel.getMinSectionY(),
				cameraSectionPos.x() - viewDistance,
				cameraSectionPos.z() - viewDistance,
				clientLevel.getMaxSectionY(),
				cameraSectionPos.x() + viewDistance,
				cameraSectionPos.z() + viewDistance
		);
	}

	@Override
	public void onInitializeClient() {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(OVERLAY_MODELS_KEY, overlayModels);
		ResourceLoader.get(PackType.CLIENT_RESOURCES).addListenerOrdering(OVERLAY_MODELS_KEY, ResourceReloaderKeys.Client.MODELS);

		ModelLoadingPlugin.register(pluginContext -> {
			pluginContext.modifyBlockModelOnLoad().register((model, context) -> {
				if (loadedModels == null) return model;
				var state = context.state();

				var overlayEntry = overlayModels.getEntry(state.getBlock());
				if (overlayEntry == null) return model;

				var overlayState = overlayEntry.definition().any();
				for (Property<?> property : state.getProperties())
					overlayState = copy(overlayState, state, property);

				var overlayModel = loadedModels.models().get(overlayState);
				if (overlayModel == null) {
					LOGGER.error("Could not find model {} from overlay model for {}", overlayEntry.id(), state);
					return model;
				}

				return new CompositeBlockstateModelRoot(List.of(
						model,
						new OverlayBlockStateModel.Unbaked(overlayModel)
				));
			});
		});

		ClientTickEvents.START_LEVEL_TICK.register(clientLevel -> {
			var player = Minecraft.getInstance().player;
			if (player == null) return;

			var newShowOverlay = Arrays.stream(InteractionHand.values())
					.anyMatch(hand -> player.getItemInHand(hand).is(Items.HONEYCOMB));
			if (showOverlay != newShowOverlay) {
				showOverlay = newShowOverlay;

				rerenderAllChunks(clientLevel);
			}
		});

		LivingEntityRenderLayerRegistrationCallback.EVENT.register((_, entityRenderer, registrationHelper, _) -> {
			if (entityRenderer instanceof CopperGolemRenderer copperGolemRenderer)
				registrationHelper.register(new CopperGolemWaxLayer(copperGolemRenderer));
		});
	}
}