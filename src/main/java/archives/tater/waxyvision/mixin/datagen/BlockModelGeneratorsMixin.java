package archives.tater.waxyvision.mixin.datagen;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

@Mixin(BlockModelGenerators.class)
public class BlockModelGeneratorsMixin {

    @WrapMethod(
            method = "createFlatItemModel(Lnet/minecraft/world/item/Item;)Lnet/minecraft/resources/Identifier;"
    )
    private Identifier skipEmpty(Item item, Operation<Identifier> original) {
        return item == Items.AIR ? ModelLocationUtils.getModelLocation(item) : original.call(item);
    }
}
