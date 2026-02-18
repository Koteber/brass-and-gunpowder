package io.github.koteber.brassandgunpowder.Event;

import net.minecraft.client.Minecraft;

import java.util.EventListener;

public interface Listener extends EventListener {
    void onEvent(Minecraft minecraft);
}
