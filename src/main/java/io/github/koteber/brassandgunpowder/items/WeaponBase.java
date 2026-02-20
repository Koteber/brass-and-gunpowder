package io.github.koteber.brassandgunpowder.items;

import io.github.koteber.brassandgunpowder.BaG;
import io.github.koteber.brassandgunpowder.Bullet;
import io.github.koteber.brassandgunpowder.Event.EventPoster;
import io.github.koteber.brassandgunpowder.SequencePart;
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
    public String loadedState = "loaded";

    public ArrayList<SequencePart> sequence = new ArrayList<>();

    protected EventPoster poster = new EventPoster();


    public float ammo;
    public float proj_speed;
    public int proj_damage;
    public float proj_spread;


    public WeaponBase(Identifier identifier, int ammo, float speed, int damage, float spread) {
        super(identifier);
        this.ammo = ammo;
        proj_speed = speed;
        proj_damage = damage;
        proj_spread = spread;
        this.maxCount = 1;
        setMaxDamage(ammo);
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
        if (minecraft.player.inventory.getSelectedItem().getItem() != this) return;
        if (!isReloading) return;
        if (sequence.isEmpty()) return;

        yDelta += mouse.deltaY;
        xDelta += mouse.deltaX;

        boolean flag = false;

        if (sequence.get(0).direction.equals(SequencePart.SequenceDirections.UP)) {
            int textureId = minecraft.textureManager.getTextureId("/assets/brassandgunpowder/gui/arrows/up_arrow.png");
            minecraft.textureManager.bindTexture(textureId);
            minecraft.inGameHud.drawTexture(centerX - 128, centerY - 128, 0, 0, 256, 256);

            yDelta -= Math.abs(xDelta);
            xDelta = 0;

            if (yDelta >= sequence.get(0).length) {
                flag = true;
            }
            else if (yDelta < 0) {
                yDelta = 0;
            }
        } else if (sequence.get(0).direction.equals(SequencePart.SequenceDirections.DOWN)) {
            int textureId = minecraft.textureManager.getTextureId("/assets/brassandgunpowder/gui/arrows/down_arrow.png");
            minecraft.textureManager.bindTexture(textureId);
            minecraft.inGameHud.drawTexture(centerX - 128, centerY - 128, 0, 0, 256, 256);

            yDelta += Math.abs(xDelta);
            xDelta = 0;

            if (yDelta <= -sequence.get(0).length) {
                flag = true;
            }
            else if (yDelta > 0) {
                yDelta = 0;
            }
        } else if (sequence.get(0).direction.equals(SequencePart.SequenceDirections.LEFT)) {
            int textureId = minecraft.textureManager.getTextureId("/assets/brassandgunpowder/gui/arrows/left_arrow.png");
            minecraft.textureManager.bindTexture(textureId);
            minecraft.inGameHud.drawTexture(centerX - 128, centerY - 128, 0, 0, 256, 256);

            xDelta += Math.abs(yDelta);
            yDelta = 0;

            if (xDelta <= -sequence.get(0).length) {
                flag = true;
            }
            else if (xDelta > 0) {
                xDelta = 0;
            }
        } else if (sequence.get(0).direction.equals(SequencePart.SequenceDirections.RIGHT)) {
            int textureId = minecraft.textureManager.getTextureId("/assets/brassandgunpowder/gui/arrows/right_arrow.png");
            minecraft.textureManager.bindTexture(textureId);
            minecraft.inGameHud.drawTexture(centerX - 128, centerY - 128, 0, 0, 256, 256);

            xDelta -= Math.abs(yDelta);
            yDelta = 0;

            if (xDelta >= sequence.get(0).length) {
                flag = true;
            }
            else if (xDelta < 0) {
                xDelta = 0;
            }
        }

        if (flag) {
            yDelta = 0;
            xDelta = 0;
            sequence.remove(0);
        }

        if (sequence.isEmpty()) {
            poster.postEvent(minecraft);
        }
    }

    public void startReloading() {
        isReloading = true;
        BaG.isReloading = true;
    }
    public void interruptReloading() {
        sequence.clear();
        isReloading = false;
        BaG.isReloading = false;
        poster.removeAll();
    }
    public void shoot(ItemStack stack, World world, PlayerEntity user) {
        if ((stack.getDamage() == stack.getMaxDamage())) return;

        stack.damage(1, user);
        world.playSound(user, "random.bow", 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
        world.spawnEntity(new Bullet(world, user, proj_speed, proj_damage, proj_spread));
        setState(stack, emptyState);
    }
    public void setState(ItemStack stack, String state) {
        stack.getStationNbt().putString("state", state);
    }

    public String getState(ItemStack stack) {
        return stack.getStationNbt().getString("state");
    }
}
