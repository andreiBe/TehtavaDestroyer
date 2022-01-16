package com.patonki.ui;

import com.patonki.util.Event;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.HashMap;

/**
 * Kuuntelee näppäinten painalluksia globaalisti, eli
 * ohjelman ei tarvitse olla edes näkyvissä.
 */
public class GlobalKeyListener implements NativeKeyListener {
    private Event<Integer> releasedEvent;
    private HashMap<Integer,Boolean> pressedKeys = new HashMap<>();
    public GlobalKeyListener() {

    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        pressedKeys.put(nativeKeyEvent.getKeyCode(),true);
    }
    public void setOnKeyReleased(Event<Integer> event) {
        releasedEvent = event;
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        pressedKeys.put(nativeKeyEvent.getKeyCode(),false);
        if (releasedEvent == null) return;
        int key = nativeKeyEvent.getKeyCode();
        Boolean cntrl = pressedKeys.get(29);
        releasedEvent.run(key, cntrl != null && cntrl);
    }

}
