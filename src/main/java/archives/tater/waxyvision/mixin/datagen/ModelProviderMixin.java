package archives.tater.waxyvision.mixin.datagen;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

@Mixin(targets = "net.minecraft.client.data.models.ModelProvider$ItemInfoCollector")
public class ModelProviderMixin {
    @WrapWithCondition(
            method = "accept",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/data/models/ModelProvider$ItemInfoCollector;register(Lnet/minecraft/world/item/Item;Lnet/minecraft/client/renderer/item/ClientItem;)V")
    )
    private boolean skipEmpty(@Coerce Object instance, Item item, ClientItem clientItem) {
        return item != Items.AIR;
    }
}
