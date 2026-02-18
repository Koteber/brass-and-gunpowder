package io.github.koteber.brassandgunpowder.items;

import io.github.koteber.brassandgunpowder.BaG;
import io.github.koteber.brassandgunpowder.Bullet;
import io.github.koteber.brassandgunpowder.SequencePart;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;
import java.awt.*;

public class LeverShotgun extends WeaponBase {
    public LeverShotgun(Identifier identifier, int _ammo, int _damage) {
        super(identifier, _ammo, _damage);
    }

    private boolean debounce_reloadScreen;

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (stack != ((PlayerEntity)entity).inventory.getSelectedItem()) return;

        super.inventoryTick(stack, world, entity, slot, selected);
        if (!selected && isReloading) {
            interruptReloading();
            return;
        }
        if (!BaG.keyState_Reload && debounce_reloadScreen) { debounce_reloadScreen = false; }
        if (!isReloading && stack.getDamage() > 0) {
            if (BaG.keyState_Reload && !debounce_reloadScreen) {
                debounce_reloadScreen = true;
                reloadZonesInitialize();
                startReloading();
            }
        }
        else if (isReloading) {
            if (BaG.keyState_Reload && !debounce_reloadScreen) {
                debounce_reloadScreen = true;
                interruptReloading();
            }
        }
    }

    @Override
    public void shoot(ItemStack stack, World world, PlayerEntity user) {
        if ((stack.getDamage() == stack.getMaxDamage())) return;

        stack.damage(1, user);
        world.playSound(user, "random.bow", 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
        world.spawnEntity(new Bullet(world, user, 5, 3, damage));
        world.spawnEntity(new Bullet(world, user, 5, 3, damage));
        world.spawnEntity(new Bullet(world, user, 5, 3, damage));
        world.spawnEntity(new Bullet(world, user, 5, 3, damage));
        world.spawnEntity(new Bullet(world, user, 5, 3, damage));
        setState(stack, emptyState);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        if (isReloading) return super.use(stack, world, user);

        if (getState(stack).equals(loadedState)) {
            shoot(stack, world, user);
        }
        else if (getState(stack).equals(emptyState) && stack.getDamage() < stack.getMaxDamage()) {
            pumpShotgun();
            startReloading();
        }
        return super.use(stack, world, user);
    }

    public void reloadZonesInitialize() {
        poster.removeAll();
        poster.addListener(this::onFinishReloading);

        sequence.clear();

        sequence.add(new SequencePart(SequencePart.SequenceDirections.UP, 30));
        sequence.add(new SequencePart(SequencePart.SequenceDirections.DOWN, 30));
        sequence.add(new SequencePart(SequencePart.SequenceDirections.LEFT, 30));
        sequence.add(new SequencePart(SequencePart.SequenceDirections.DOWN, 30));

    }
    public void onFinishReloading(Minecraft minecraft) {
        isReloading = false;
        BaG.isReloading = false;
        minecraft.player.inventory.getSelectedItem().setDamage(0);
    }

    public void pumpShotgun() {
        poster.removeAll();
        poster.addListener(this::onPumpShotgun);

        sequence.clear();

        sequence.add(new SequencePart(SequencePart.SequenceDirections.DOWN, 40));
        sequence.add(new SequencePart(SequencePart.SequenceDirections.UP, 40));
    }
    public void onPumpShotgun(Minecraft minecraft) {
        setState(minecraft.player.inventory.getSelectedItem(), loadedState);
        isReloading = false;
        BaG.isReloading = false;
    }
}
