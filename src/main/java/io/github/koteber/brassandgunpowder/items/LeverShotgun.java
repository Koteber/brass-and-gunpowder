package io.github.koteber.brassandgunpowder.items;

import com.mojang.datafixers.TypeRewriteRule;
import io.github.koteber.brassandgunpowder.BaG;
import net.glasslauncher.mods.gcapi3.mixin.client.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.ScreenScaler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;
import org.lwjgl.util.vector.Vector2f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.print.attribute.standard.Finishings;
import java.awt.*;
import java.util.ArrayList;

public class LeverShotgun extends WeaponBase {
    public LeverShotgun(Identifier identifier, int _ammo) {
        super(identifier, _ammo);
    }

    private float yDelta;
    private float xDelta;

    private boolean debounce_unselectedCheck;
    private boolean debounce_reloadScreen;
    public boolean debounce_repositionZones;

    public int currentReloadStage;
    @Override
    public void reloadHandler(float tickDelta, boolean screenOpen, int centerX, int centerY, int mouseX, int mouseY, Minecraft minecraft, CallbackInfo ci) {
        if (!isReloading) return;

        yDelta += mouse.deltaY;
        xDelta += mouse.deltaX;

        if (currentReloadStage == 0) {
            int textureId = minecraft.textureManager.getTextureId("/assets/brassandgunpowder/gui/arrows/up_arrow.png");
            minecraft.textureManager.bindTexture(textureId);
            minecraft.inGameHud.drawTexture(centerX - 128, centerY - 128, 0, 0, 256, 256);

            if (yDelta >= 25) {
                yDelta = 0;
                xDelta = 0;
                currentReloadStage = 1;
            }
        }
        else if (currentReloadStage == 1) {
            int textureId = minecraft.textureManager.getTextureId("/assets/brassandgunpowder/gui/arrows/down_arrow.png");
            minecraft.textureManager.bindTexture(textureId);
            minecraft.inGameHud.drawTexture(centerX - 128, centerY - 128, 0, 0, 256, 256);

            if (yDelta <= -25) {
                yDelta = 0;
                xDelta = 0;
                currentReloadStage = 2;
            }
        }
        else if (currentReloadStage == 2) {
            int textureId = minecraft.textureManager.getTextureId("/assets/brassandgunpowder/gui/arrows/left_arrow.png");
            minecraft.textureManager.bindTexture(textureId);
            minecraft.inGameHud.drawTexture(centerX - 128, centerY - 128, 0, 0, 256, 256);

            if (xDelta <= -25) {
                yDelta = 0;
                xDelta = 0;
                currentReloadStage = 0;
                finishReloading(minecraft.player.inventory.getSelectedItem());
            }
        }
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
//        BaG.LOGGER.info(getState(stack) + " / " + isReloading + " / " + BaG.isReloading + " / " + BaG.keyState_Reload + " / " + debounce_reloadScreen);
        BaG.LOGGER.info(currentReloadStage + " / " + xDelta + " / " + yDelta);
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!selected && isReloading) {
            interruptReloading(stack);
            return;
        }
        if (!BaG.keyState_Reload && debounce_reloadScreen) { debounce_reloadScreen = false; }
        if (getState(stack).equals("empty")) {
            if (BaG.keyState_Reload && !debounce_reloadScreen) {
                debounce_reloadScreen = true;
                reloadZonesInitialize();
                startReloading(stack);
            }
        }
        else if (getState(stack).equals("reloading")) {
            if (BaG.keyState_Reload && !debounce_reloadScreen) {
                debounce_reloadScreen = true;
                interruptReloading(stack);
            }
        }
    }

    @Override
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        if (getState(stack).equals(loadedState)) {
            shoot(stack, world, user);
        }
        return super.use(stack, world, user);
    }

    public void reloadZonesInitialize() {
        sequence.add(SequenceDirections.UP);
        sequence.add(SequenceDirections.DOWN);
        sequence.add(SequenceDirections.LEFT);
    }

}
