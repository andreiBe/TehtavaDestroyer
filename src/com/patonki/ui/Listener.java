package com.patonki.ui;

import com.patonki.util.Event;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Kuuntelee näppäinten painalluksia. Tarkoitus ajaa eri Threadissa.
 */
public class Listener implements Runnable{
    private GlobalKeyListener globalKeyListener;
    private final Event<Integer> event;

    public Listener(Event<Integer> event) {
        this.event = event;
    }

    @Override
    public void run() {
        try {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.WARNING);
            logger.setUseParentHandlers(false);

            GlobalScreen.registerNativeHook();
            globalKeyListener = new GlobalKeyListener();
            GlobalScreen.addNativeKeyListener(globalKeyListener);
            globalKeyListener.setOnKeyReleased(event);
            //GlobalScreen.addNativeMouseListener(new GlobalMouseListener(script));
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }
}

