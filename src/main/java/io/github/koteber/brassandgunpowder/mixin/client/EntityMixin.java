package io.github.koteber.brassandgunpowder.mixin.client;

import io.github.koteber.brassandgunpowder.BaG;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow
    public float yaw;

    @Shadow
    public float prevYaw;

    @Shadow
    public float pitch;

    @Shadow
    public float prevPitch;

    @Inject(
            method = "changeLookDirection",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void reload_disableCamMovement(float cursorDeltaX, float cursorDeltaY, CallbackInfo ci) {
        if (BaG.isReloading) {
            ci.cancel();
        }
    }
}
