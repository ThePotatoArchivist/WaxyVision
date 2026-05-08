package archives.tater.waxyvision.datagen;

import archives.tater.waxyvision.WaxyVisionKeys;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class LangGenerator extends FabricLanguageProvider {
    public LangGenerator(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(packOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(WaxyVisionKeys.KEY_CATEGORY.id().toLanguageKey("key.category"), "WaxyVision");
        translationBuilder.add(WaxyVisionKeys.ENABLE_KEY.getName(), "Toggle Enabled");
        translationBuilder.add(WaxyVisionKeys.REQUIRE_ITEM_KEY.getName(), "Toggle Item Required");
        translationBuilder.add(WaxyVisionKeys.ENABLED_TRANSLATION, "WaxyVision %s");
        translationBuilder.add(WaxyVisionKeys.REQUIRE_TRANSLATION, "WaxyVision: Item Requirement %s");
        translationBuilder.add(WaxyVisionKeys.ON_TRANSLATION, "ON");
        translationBuilder.add(WaxyVisionKeys.OFF_TRANSLATION, "OFF");
    }
}
