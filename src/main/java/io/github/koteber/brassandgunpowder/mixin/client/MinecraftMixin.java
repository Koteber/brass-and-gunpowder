package io.github.koteber.brassandgunpowder.mixin.client;

import io.github.koteber.brassandgunpowder.BaG;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(
            method = "handleMouseDown",
            at = @At(value = "HEAD")
    )
    private void brassAndGunpowder_handleMouseDown(int button, boolean holdingAttack, CallbackInfo ci) {
        BaG.isHoldingLMB = holdingAttack;
    }
}
