package io.github.koteber.brassandgunpowder.items;

import io.github.koteber.brassandgunpowder.BaG;
import io.github.koteber.brassandgunpowder.SequencePart;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;

public class Revolver extends WeaponBase {
    public Revolver(Identifier identifier, int ammo, float speed, int damage, float spread) {
        super(identifier, ammo, speed, damage, spread);
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
                reloadSequence(true, stack.getDamage() == 1);
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
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        if (isReloading) return super.use(stack, world, user);

        if (getState(stack).equals(loadedState)) {
            shoot(stack, world, user);
        }
        else if (getState(stack).equals(emptyState) && stack.getDamage() < stack.getMaxDamage()) {
            leverCocking();
            startReloading();
        }
        return super.use(stack, world, user);
    }

    public void reloadSequence(boolean start, boolean finish) {
        poster.removeAll();
        poster.addListener(this::onReloadFinished);

        if (start) {
            sequence.clear();
            sequence.add(new SequencePart(SequencePart.SequenceDirections.LEFT, 80));
        }

        sequence.add(new SequencePart(SequencePart.SequenceDirections.UP, 120));
        sequence.add(new SequencePart(SequencePart.SequenceDirections.DOWN, 120));

        if (finish) {
            sequence.add(new SequencePart(SequencePart.SequenceDirections.RIGHT, 80));
        }
    }
    public void onReloadFinished(Minecraft minecraft) {
        if (minecraft.player.inventory.getSelectedItem().getDamage() == 2) {
            minecraft.player.inventory.getSelectedItem().setDamage(minecraft.player.inventory.getSelectedItem().getDamage() - 1);
            reloadSequence(false,true);
        }
        else if (minecraft.player.inventory.getSelectedItem().getDamage() > 1) {
            minecraft.player.inventory.getSelectedItem().setDamage(minecraft.player.inventory.getSelectedItem().getDamage() - 1);
            reloadSequence(false,false);
        }
        else {
            isReloading = false;
            BaG.isReloading = false;
            minecraft.player.inventory.getSelectedItem().setDamage(0);
        }
    }

    public void leverCocking() {
        sequence.clear();
        sequence.add(new SequencePart(SequencePart.SequenceDirections.DOWN, 75));

        poster.removeAll();
        poster.addListener(this::onCockingFinished);
    }
    public void onCockingFinished(Minecraft minecraft) {
        setState(minecraft.player.inventory.getSelectedItem(), loadedState);
        isReloading = false;
        BaG.isReloading = false;
    }
}
