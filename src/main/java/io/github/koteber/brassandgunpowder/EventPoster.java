package io.github.koteber.brassandgunpowder;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

interface Listener extends EventListener {
    void onMessageReceived();
}
public class EventPoster {
    private final List<Listener> listeners = new ArrayList<>();

    // Регистрация слушателя
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    // Метод для "постинга" (рассылки) события
    public void postEvent() {
        for (Listener listener : listeners) {
            listener.onMessageReceived();
        }
    }
}
