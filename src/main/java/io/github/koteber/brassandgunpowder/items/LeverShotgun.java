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
        sequence.clear();

        sequence.add(SequenceDirections.UP);
        sequence.add(SequenceDirections.DOWN);
        sequence.add(SequenceDirections.LEFT);
        sequence.add(SequenceDirections.DOWN);
    }

}
