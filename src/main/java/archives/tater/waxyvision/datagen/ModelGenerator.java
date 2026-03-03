package archives.tater.waxyvision.datagen;

import archives.tater.waxyvision.mixin.datagen.BlockModelGeneratorsAccessor;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.data.BlockFamilies;
import net.minecraft.world.level.block.Block;

public class ModelGenerator extends FabricModelProvider {

    public ModelGenerator(FabricDataOutput output) {
        super(output);
    }

    private void createBars(BlockModelGenerators blockModelGenerators, Block block, Block sideTexture, Block topTexture) {
        TextureMapping side = TextureMapping.bars(sideTexture).put(TextureSlot.EDGE, ModelLocationUtils.getModelLocation(topTexture));
        TextureMapping top = TextureMapping.bars(topTexture);
        blockModelGenerators.createBars(
                block,
                ModelTemplates.BARS_POST_ENDS.create(block, top, blockModelGenerators.modelOutput),
                ModelTemplates.BARS_POST.create(block, top, blockModelGenerators.modelOutput),
                ModelTemplates.BARS_CAP.create(block, top, blockModelGenerators.modelOutput),
                ModelTemplates.BARS_CAP_ALT.create(block, top, blockModelGenerators.modelOutput),
                ModelTemplates.BARS_POST_SIDE.create(block, side, blockModelGenerators.modelOutput),
                ModelTemplates.BARS_POST_SIDE_ALT.create(block, side, blockModelGenerators.modelOutput)
        );
    }

    private void createLightningRod(BlockModelGenerators blockModelGenerators, Block block) {
        var variant = BlockModelGenerators.plainVariant(ModelTemplates.LIGHTNING_ROD.create(block, TextureMapping.defaultTexture(block), blockModelGenerators.modelOutput));
        blockModelGenerators.blockStateOutput
                .accept(
                        MultiVariantGenerator.dispatch(block, variant)
                                .with(BlockModelGeneratorsAccessor.getROTATIONS_COLUMN_WITH_FACING())
                );
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
        blockModelGenerators.createAxisAlignedPillarBlockCustomModel(FakeBlocks.CHAIN,
                BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(FakeBlocks.CHAIN))
        );
        blockModelGenerators.createLantern(FakeBlocks.LANTERN);
        createBars(blockModelGenerators, FakeBlocks.BARS, FakeBlocks.CUBE, FakeBlocks.BARS);
        createLightningRod(blockModelGenerators, FakeBlocks.LIGHTNING_ROD);
        blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(FakeBlocks.CHEST, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(FakeBlocks.CHEST))));
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {

    }

}
