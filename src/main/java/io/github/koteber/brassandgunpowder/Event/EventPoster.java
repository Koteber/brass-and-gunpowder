package io.github.koteber.brassandgunpowder.Event;

import io.github.koteber.brassandgunpowder.BaG;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class EventPoster {
    private final List<Listener> listeners = new ArrayList<>();

    public void addListener(Listener listener) {
        listeners.add(listener);
    }
    public void removeAll() {
        listeners.clear();
    }
    public void postEvent(Minecraft minecraft) {
        for (Listener listener : listeners) {
            listener.onEvent(minecraft);
        }
    }
}
