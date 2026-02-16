package io.github.koteber.brassandgunpowder.items;

import io.github.koteber.brassandgunpowder.BaG;
import io.github.koteber.brassandgunpowder.SliderZone;
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

import java.awt.*;
import java.util.ArrayList;

public class LeverShotgun extends WeaponBase {
    public LeverShotgun(Identifier identifier, int _ammo) {
        super(identifier, _ammo);
    }
    private Mouse mouse;

    private boolean debounce_unselectedCheck;
    private boolean debounce_reloadScreen;
    public boolean debounce_repositionZones;

    public ArrayList<Rectangle> zones = new ArrayList<>();
    public ArrayList<SliderZone> sliders = new ArrayList<>();

    @Override
    public void reloadHandler(float tickDelta, boolean screenOpen, int centerX, int centerY, int mouseX, int mouseY, Minecraft minecraft, CallbackInfo ci) {
        if (!isReloading) return;

        if (zones.isEmpty()) {
            finishReloading(minecraft.player.inventory.getSelectedItem());
            return;
        }
        for (Rectangle zone : zones) {
            minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId("nothing.png"));
            minecraft.inGameHud.drawTexture(zone.x, zone.y, 0, 0, zone.width, zone.height);
            if (BaG.isInsideRectangle(zone, mouseX, mouseY)) {
                zones.remove(zone);
                break;
            }
        }
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!selected && isReloading) {
            interruptReloading(stack);
            return;
        }

        if (getState(stack).equals("empty")) {
            if (!BaG.keyState_Reload && debounce_reloadScreen) { debounce_reloadScreen = false; }
            if (BaG.keyState_Reload && !debounce_reloadScreen) {
                debounce_reloadScreen = true;
                reloadZonesInitialize();
                startReloading(stack);
            }
        }
        else if (getState(stack).equals("reloading")) {
            if (!BaG.keyState_Reload && debounce_reloadScreen) { debounce_reloadScreen = false; }
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
        zones.clear();

        zones.add(new Rectangle(0,0, 15,15));

        zones.add(new Rectangle(50,50, 25,25));
        zones.add(new Rectangle(-50,50, 25,25));
        zones.add(new Rectangle(50,-50, 25,25));
        zones.add(new Rectangle(-50,-50, 25,25));

        zones.add(new Rectangle(0,75, 25,25));
        zones.add(new Rectangle(0,-75, 25,25));
        zones.add(new Rectangle(75,0, 25,25));
        zones.add(new Rectangle(-75,0, 25,25));

        sliders.clear();

        ArrayList<Vector2f> points = new ArrayList<>();
        points.add(new Vector2f(-100, 100));
        points.add(new Vector2f(-100, -100));

        sliders.add(new SliderZone(points, 15));

        ScreenScaler screenScaler = new ScreenScaler(MinecraftAccessor.getInstance().options, MinecraftAccessor.getInstance().displayWidth, MinecraftAccessor.getInstance().displayHeight);
        int centerX = screenScaler.getScaledWidth() / 2;
        int centerY = screenScaler.getScaledHeight() / 2;
        for (Rectangle zone : zones) {
            zone.x = centerX - (int)zone.getCenterX();
            zone.y = centerY - (int)zone.getCenterY();
        }
    }

}
