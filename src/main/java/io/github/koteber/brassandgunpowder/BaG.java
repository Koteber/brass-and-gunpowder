package io.github.koteber.brassandgunpowder;

import io.github.koteber.brassandgunpowder.items.LeverShotgun;
import io.github.koteber.brassandgunpowder.screen.ReloadScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.client.registry.GuiHandlerRegistry;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class BaG {
    @SuppressWarnings("UnstableApiUsage")
    public static final Namespace NAMESPACE = Namespace.resolve();

    public static final Logger LOGGER = NAMESPACE.getLogger();

    public static Item[] items;
    public static Item item_LEVER_SHOTGUN;

    //region Reloading
    public static KeyBinding keybind_Reload;
    public static boolean keyState_Reload;
    public static boolean isReloading;
    //endregion

    public static boolean isInsideRectangle(Rectangle rect, float mouseX, float mouseY) {
        return (mouseX >= rect.getMinX() && mouseX <= rect.getMaxX())
                && (mouseY >= rect.getMinY() && mouseY <= rect.getMaxY());
    }

    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        item_LEVER_SHOTGUN = new LeverShotgun(NAMESPACE.id("lever_shotgun"), 30).setTranslationKey(NAMESPACE, "lever_shotgun");

        items = new Item[]{
                item_LEVER_SHOTGUN
        };
    }
    @Environment(EnvType.CLIENT)
    @EventListener
    public void registerKeybinds(KeyBindingRegisterEvent event) {
        keybind_Reload = new KeyBinding("key.brassandgunpowder.reload", Keyboard.KEY_R);

        event.keyBindings.add(keybind_Reload);
    }
    @Environment(EnvType.CLIENT)
    @EventListener
    public void keybindChecker(KeyStateChangedEvent event) {
        String pressedKey = Keyboard.getKeyName(Keyboard.getEventKey());

        String reloadKey = Keyboard.getKeyName(keybind_Reload.code);

        if (pressedKey.equals(reloadKey)) {
            keyState_Reload = Keyboard.getEventKeyState();
        }
    }
}
