package io.github.koteber.brassandgunpowder.items;

import io.github.koteber.brassandgunpowder.BaG;
import io.github.koteber.brassandgunpowder.screen.ReloadScreen;
import io.github.koteber.brassandgunpowder.screen.ReloadScreenHandler;
import net.glasslauncher.mods.gcapi3.mixin.client.MinecraftAccessor;
import net.minecraft.client.Mouse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;

import java.awt.*;
import java.util.ArrayList;

public class LeverShotgun extends TemplateItem {
    public LeverShotgun(Identifier identifier) {
        super(identifier);
        this.maxCount = 1;
        setMaxDamage(1);
    }
    private Mouse mouse;
    private float xDeltaStore;
    private float yDeltaStore;

    public ArrayList<Rectangle> zones = new ArrayList<>();

    private boolean openedReloadScreen;
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (getState(stack).isEmpty()) {
            stack.setDamage(1);
            setState("empty", stack);
            return;
        }

        if (((PlayerEntity)entity).inventory.getSelectedItem() == null
            || ((PlayerEntity)entity).inventory.getSelectedItem().getItem() != this) {
            BaG.setReloadHudState(false);
            return;
        }

        if (mouse == null) {
            mouse = MinecraftAccessor.getInstance().mouse;
            return;
        }
        xDeltaStore += mouse.deltaX;
        yDeltaStore += mouse.deltaY;

        if (BaG.keyState_Reload && !openedReloadScreen) {
            openedReloadScreen = true;
            reloadInitialize();
            BaG.setReloadHudState(!BaG.reloadHudState);
        }
        if (!BaG.keyState_Reload && openedReloadScreen) { openedReloadScreen = false; }
    }

    @Override
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        return super.use(stack, world, user);
    }
    public void setState(String state, ItemStack stack) {
        stack.getStationNbt().putString("state", state);
    }

    public String getState(ItemStack stack) {
        return stack.getStationNbt().getString("state");
    }

    public void reloadInitialize() {
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

        BaG.setDrawZones(zones);
    }
}
