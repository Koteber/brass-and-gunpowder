package io.github.koteber.brassandgunpowder.mixin.client;

import io.github.koteber.brassandgunpowder.BaG;
import io.github.koteber.brassandgunpowder.items.WeaponBase;
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
    public void brassAndGunpowder_renderReloadHud(float tickDelta, boolean screenOpen, int mouseX, int mouseY, CallbackInfo ci) {
        fill(100,100, mouseX, mouseY, 2);
        drawHorizontalLine(200, 400, 500, 3);
        if (minecraft.player.inventory.getSelectedItem() == null) {
            BaG.isReloading = false;
            return;
        }
        if (!WeaponBase.class.isAssignableFrom(minecraft.player.inventory.getSelectedItem().getItem().getClass())) {
            BaG.isReloading = false;
            return;
        }

        WeaponBase weapon = (WeaponBase) minecraft.player.inventory.getSelectedItem().getItem();
        BaG.isReloading = weapon.isReloading;

        if (!weapon.isReloading) return;

        ScreenScaler screenScaler = new ScreenScaler(minecraft.options, minecraft.displayWidth, minecraft.displayHeight);
        int centerX = screenScaler.getScaledWidth() / 2;
        int centerY = screenScaler.getScaledHeight() / 2;

        int textureId = minecraft.textureManager.getTextureId("/assets/brassandgunpowder/gui/reloadScreen.png");
        minecraft.textureManager.bindTexture(textureId);
        drawTexture(centerX - 128, centerY - 128, 0, 0, 256, 256);

        weapon.reloadHandler(tickDelta, screenOpen, centerX, centerY, mouseX, mouseY, minecraft, ci);
    }
}
