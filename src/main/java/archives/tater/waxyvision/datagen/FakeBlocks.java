package archives.tater.waxyvision.datagen;

import archives.tater.waxyvision.WaxyVision;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.function.Function;

public class FakeBlocks {

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
    public static final Block CHAIN = register("chain", ChainBlock::new);
    public static final Block LANTERN = register("lantern", LanternBlock::new);
    public static final Block BARS = register("bars", IronBarsBlock::new);
    public static final Block LIGHTNING_ROD = register("lightning_rod", LightningRodBlock::new);
    public static final Block CHEST = register("chest", LightningRodBlock::new);

    public static void init() {

    }
}
