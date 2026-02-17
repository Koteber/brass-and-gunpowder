package io.github.koteber.brassandgunpowder.items;

import io.github.koteber.brassandgunpowder.BaG;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;

public class Revolver extends WeaponBase {
    public Revolver(Identifier identifier, int _ammo) {
        super(identifier, _ammo);
    }

    private boolean debounce_reloadScreen;

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!selected && isReloading) {
            interruptReloading(stack);
            return;
        }
        if (!BaG.keyState_Reload && debounce_reloadScreen) { debounce_reloadScreen = false; }
        if (stack.getDamage() > 0) {
            if (BaG.keyState_Reload && !debounce_reloadScreen) {
                debounce_reloadScreen = true;
                reloadSequence(true, stack.getDamage() == 1);
                startReloading(stack);
            }
        }
        else if (getState(stack).equals(reloadingState)) {
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

    @Override
    public void finishReloading(ItemStack stack) {
        if (stack.getDamage() == 2) {
            stack.setDamage(stack.getDamage() - 1);
            reloadSequence(false,true);
        }
        else if (stack.getDamage() > 1) {
            stack.setDamage(stack.getDamage() - 1);
            reloadSequence(false,false);
        }
        else {
            isReloading = false;
            BaG.isReloading = false;
            stack.setDamage(0);
            setState(stack, loadedState);
        }
    }

    public void reloadSequence(boolean start, boolean finish) {
        if (start) {
            sequence.clear();
            sequence.add(SequenceDirections.LEFT);
        }

        sequence.add(SequenceDirections.UP);
        sequence.add(SequenceDirections.DOWN);

        if (finish) {
            sequence.add(SequenceDirections.RIGHT);
        }
    }
}
