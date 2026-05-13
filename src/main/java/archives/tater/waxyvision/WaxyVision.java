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
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.CopperGolemRenderer;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WaxyVision implements ClientModInitializer {
	public static final String MOD_ID = "waxyvision";
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final WaxyVisionConfig CONFIG = WaxyVisionConfig.createToml(
			FabricLoader.getInstance().getConfigDir(),
			"",
			MOD_ID,
			WaxyVisionConfig.class
	);

	private static HolderSet<Item> waxItems = resolveItems(CONFIG.waxItems.value());
	static {
		CONFIG.waxItems.registerCallback(tracked -> {
			waxItems = resolveItems(tracked.value());
		});
	}

	public static final Identifier OVERLAY_MODELS_KEY = id("overlay_models");
	public static final OverlayModels overlayModels = new OverlayModels();

	public static boolean showOverlay = false;

	public static final RenderStateDataKey<Boolean> WAXED = RenderStateDataKey.create(() -> MOD_ID + ":waxed");

	@ApiStatus.Internal
	@Nullable
	public static BlockStateModelLoader.LoadedModels loadedModels;

	private static <T extends Comparable<T>> BlockState copy(BlockState target, BlockState source, Property<T> property) {
		return target.trySetValue(property, source.getValue(property));
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

	private static boolean isHoldingWaxItem(Player player) {
		return player.getMainHandItem().is(waxItems) || player.getOffhandItem().is(waxItems);
	}

	private static HolderSet.Direct<Item> resolveItems(Collection<String> itemIds) {
		return HolderSet.direct(itemIds.stream()
				.map(Identifier::tryParse)
				.filter(Objects::nonNull)
				.map(BuiltInRegistries.ITEM::get)
				.flatMap(Optional::stream)
				.toList());
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

		WaxyVisionKeys.init();

		ClientTickEvents.START_LEVEL_TICK.register(clientLevel -> {
			var player = Minecraft.getInstance().player;
			if (player == null) return;

			var newShowOverlay = CONFIG.enabled.value() && (!CONFIG.requireItem.value() || isHoldingWaxItem(player));
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