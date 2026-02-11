package io.github.koteber.brassandgunpowder.mixin.client;

import io.github.koteber.brassandgunpowder.BaG;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.ScreenScaler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawContext {

    @Shadow
    private Minecraft minecraft;

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/ClientPlayerEntity;getSleepTimer()I",
                    ordinal = 0
            ),
            cancellable = true
    )
    public void brassAndGunpowder_renderScore(float tickDelta, boolean screenOpen, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.minecraft.player.health > 0 && !this.minecraft.options.debugHud && BaG.reloadHudState) {
            ScreenScaler screenScaler = new ScreenScaler(this.minecraft.options, this.minecraft.displayWidth, this.minecraft.displayHeight);
            int centerX = screenScaler.getScaledWidth() / 2;
            int centerY = screenScaler.getScaledHeight() / 2;

            int textureId = this.minecraft.textureManager.getTextureId("/assets/brassandgunpowder/gui/reloadScreen.png");
            this.minecraft.textureManager.bindTexture(textureId);
            this.drawTexture(centerX - 128, centerY - 128, 0, 0, 256, 256);

            ArrayList<Rectangle> zones = BaG.getDrawZones();
            for (Rectangle zone : zones) {
                this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId("nothing.png"));
                this.drawTexture(centerX - (int)zone.getCenterX(), centerY - (int)zone.getCenterY(), 0, 0, zone.width, zone.height);
            }
        }
        else {
            /* nothing */
        }
    }
}
