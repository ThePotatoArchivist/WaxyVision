package archives.tater.waxyvision.mixin.sync;

import archives.tater.waxyvision.WaxyVisionCommon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin extends BlockEntity {
    public SignBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Shadow
    public abstract boolean isWaxed();

    @Unique
    private void updateWaxed() {
        if (level != null)
            WaxyVisionCommon.setWaxed(this, isWaxed());
    }

    @Inject(
            method = "loadAdditional",
            at = @At("TAIL")
    )
    private void updateWaxed(ValueInput input, CallbackInfo ci) {
        updateWaxed();
    }

    @Inject(
            method = "setWaxed",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SignBlockEntity;markUpdated()V")
    )
    private void updateWaxed(boolean isWaxed, CallbackInfoReturnable<Boolean> cir) {
        updateWaxed();
    }
}
