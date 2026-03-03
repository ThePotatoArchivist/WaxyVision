package archives.tater.waxyvision.mixin.sync;

import archives.tater.waxyvision.WaxyVisionCommon;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;

@Mixin(CopperGolem.class)
public class CopperGolemMixin extends AbstractGolem {
    @Shadow
    @Final
    private static long IGNORE_WEATHERING_TICK;

    @Shadow
    private long nextWeatheringTick;

    protected CopperGolemMixin(EntityType<? extends AbstractGolem> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private void updateWaxed() {
        WaxyVisionCommon.setWaxed(this, nextWeatheringTick == IGNORE_WEATHERING_TICK);
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void updateWaxed(ValueInput input, CallbackInfo ci) {
        updateWaxed();
    }

    @Inject(
            method = "mobInteract",
            at = {
                    @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/golem/CopperGolem;usePlayerItem(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V"),
                    @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V")
            }
    )
    private void updateWaxed(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        updateWaxed();
    }

    @Inject(
            method = "thunderHit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V")
    )
    private void updateWaxed(ServerLevel level, LightningBolt lightning, CallbackInfo ci) {
        updateWaxed();
    }
}
