package archives.tater.waxyvision.datagen;

import archives.tater.waxyvision.WaxyVision;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.function.Function;

public class ModelGenerator extends FabricModelProvider {

    public ModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        var family = BlockFamilies.familyBuilder(FakeBlocks.CUBE)
                .stairs(FakeBlocks.STAIRS)
                .slab(FakeBlocks.SLAB)
                .door(FakeBlocks.DOOR)
                .trapdoor(FakeBlocks.TRAPDOOR)
                .getFamily();
        blockModelGenerators.family(family.getBaseBlock())
                .generateFor(family);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {

    }

    public static class FakeBlocks {

        private static Block register(String path, Function<BlockBehaviour.Properties, Block> factory) {
            var key = ResourceKey.create(Registries.BLOCK, WaxyVision.id(path));
            var block = factory.apply(BlockBehaviour.Properties.of().setId(key));
            return Registry.register(BuiltInRegistries.BLOCK, key, block);
        }

        public static final Block CUBE = register("cube", Block::new);
        public static final Block STAIRS = register("stairs", properties -> new StairBlock(CUBE.defaultBlockState(), properties));
        public static final Block SLAB = register("slab", SlabBlock::new);
        public static final Block DOOR = register("door", properties -> new DoorBlock(BlockSetType.PALE_OAK, properties));
        public static final Block TRAPDOOR = register("trapdoor", properties -> new TrapDoorBlock(BlockSetType.PALE_OAK, properties));

        public static void init() {

        }
    }
}
