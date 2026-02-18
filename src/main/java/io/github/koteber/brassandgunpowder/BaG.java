package io.github.koteber.brassandgunpowder;

import io.github.koteber.brassandgunpowder.items.LeverShotgun;
import io.github.koteber.brassandgunpowder.items.Revolver;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@SuppressWarnings("unused")
public class BaG {
    @SuppressWarnings("UnstableApiUsage")
    public static final Namespace NAMESPACE = Namespace.resolve();

    public static final Logger LOGGER = NAMESPACE.getLogger();

    public static Item[] items;
    public static Item item_LEVER_SHOTGUN;
    public static Item item_REVOLVER;

    //region Reloading
    public static KeyBinding keybind_Reload;
    public static boolean keyState_Reload;
    public static boolean isReloading;
    //endregion

    public static boolean isHoldingLMB;


    public static boolean isInsideRectangle(Rectangle rect, float mouseX, float mouseY) {
        return (mouseX >= rect.getMinX() && mouseX <= rect.getMaxX())
                && (mouseY >= rect.getMinY() && mouseY <= rect.getMaxY());
    }

    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        item_LEVER_SHOTGUN = new LeverShotgun(NAMESPACE.id("lever_shotgun"), 15, 5).setTranslationKey(NAMESPACE, "lever_shotgun");
        item_REVOLVER = new Revolver(NAMESPACE.id("revolver"), 6, 12).setTranslationKey(NAMESPACE, "revolver");

        items = new Item[]{
                item_LEVER_SHOTGUN,
                item_REVOLVER
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
