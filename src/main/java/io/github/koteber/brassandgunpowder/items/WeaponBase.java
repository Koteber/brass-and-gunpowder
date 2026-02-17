package io.github.koteber.brassandgunpowder.items;

import com.mojang.datafixers.TypeRewriteRule;
import io.github.koteber.brassandgunpowder.BaG;
import io.github.koteber.brassandgunpowder.EventPoster;
import net.glasslauncher.mods.gcapi3.mixin.client.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Mouse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

public abstract class WeaponBase extends TemplateItem {

    protected Mouse mouse;
    protected float yDelta;
    protected float xDelta;

    public boolean isReloading;

    public String emptyState = "empty";
    public String reloadingState = "reloading";
    public String loadedState = "loaded";

    public enum SequenceDirections {
        LEFT, RIGHT, UP, DOWN;
    }
    public ArrayList<SequenceDirections> sequence = new ArrayList<>();

    protected EventPoster poster = new EventPoster();

    public WeaponBase(Identifier identifier, int _ammo) {
        super(identifier);
        this.maxCount = 1;
        setMaxDamage(_ammo);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (mouse == null) mouse = MinecraftAccessor.getInstance().mouse;
        if (getState(stack).isEmpty()) {
            stack.setDamage(stack.getMaxDamage());
            setState(stack,"empty");
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    public void reloadHandler(float tickDelta, boolean screenOpen, int centerX, int centerY, int mouseX, int mouseY, Minecraft minecraft, CallbackInfo ci) {
        if (!isReloading) return;

        yDelta += mouse.deltaY;
        xDelta += mouse.deltaX;

        boolean flag = false;

        if (sequence.get(0).equals(SequenceDirections.UP)) {
            int textureId = minecraft.textureManager.getTextureId("/assets/brassandgunpowder/gui/arrows/up_arrow.png");
            minecraft.textureManager.bindTexture(textureId);
            minecraft.inGameHud.drawTexture(centerX - 128, centerY - 128, 0, 0, 256, 256);

            if (yDelta >= 25) {
                flag = true;
            }
        } else if (sequence.get(0).equals(SequenceDirections.DOWN)) {
            int textureId = minecraft.textureManager.getTextureId("/assets/brassandgunpowder/gui/arrows/down_arrow.png");
            minecraft.textureManager.bindTexture(textureId);
            minecraft.inGameHud.drawTexture(centerX - 128, centerY - 128, 0, 0, 256, 256);

            if (yDelta <= -25) {
                flag = true;
            }
        } else if (sequence.get(0).equals(SequenceDirections.LEFT)) {
            int textureId = minecraft.textureManager.getTextureId("/assets/brassandgunpowder/gui/arrows/left_arrow.png");
            minecraft.textureManager.bindTexture(textureId);
            minecraft.inGameHud.drawTexture(centerX - 128, centerY - 128, 0, 0, 256, 256);

            if (xDelta <= -25) {
                flag = true;
            }
        } else if (sequence.get(0).equals(SequenceDirections.RIGHT)) {
            int textureId = minecraft.textureManager.getTextureId("/assets/brassandgunpowder/gui/arrows/right_arrow.png");
            minecraft.textureManager.bindTexture(textureId);
            minecraft.inGameHud.drawTexture(centerX - 128, centerY - 128, 0, 0, 256, 256);

            if (xDelta >= 25) {
                flag = true;
            }
        }

        if (flag) {
            yDelta = 0;
            xDelta = 0;
            sequence.remove(0);
        }

        if (sequence.isEmpty()) {
            poster.postEvent();
            finishReloading(minecraft.player.inventory.getSelectedItem());
        }
    }

    public void startReloading(ItemStack stack) {
        isReloading = true;
        BaG.isReloading = true;
        setState(stack, reloadingState);
    }
    public void interruptReloading(ItemStack stack) {
        sequence.clear();
        isReloading = false;
        BaG.isReloading = false;
        setState(stack, emptyState);
    }
    public void finishReloading(ItemStack stack) {
        isReloading = false;
        BaG.isReloading = false;
        stack.setDamage(0);
        setState(stack, loadedState);
    }
    public void shoot(ItemStack stack, World world, PlayerEntity user) {
        if ((stack.getDamage() == stack.getMaxDamage())) return;

        stack.damage(1, user);
        world.playSound(user, "random.bow", 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
        world.spawnEntity(new ArrowEntity(world, user));

        if ((stack.getDamage() == stack.getMaxDamage())) setState(stack, emptyState);
    }
    public void setState(ItemStack stack, String state) {
        stack.getStationNbt().putString("state", state);
    }

    public String getState(ItemStack stack) {
        return stack.getStationNbt().getString("state");
    }
}
