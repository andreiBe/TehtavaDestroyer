package com.patonki.ui;

import com.patonki.Kirjoittaja;
import com.patonki.util.KeyListener;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.*;
import java.util.List;

/**
 * Kuuntelee näppäinten painalluksia globaalisti, eli
 * ohjelman ei tarvitse olla edes näkyvissä.
 */
public class GlobalKeyListener implements NativeKeyListener {
    private final HashMap<Integer,Boolean> pressedKeys = new HashMap<>();
    private final List<KeyListener<Integer>> listeners = new ArrayList<>();
    public GlobalKeyListener() {

    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        pressedKeys.put(nativeKeyEvent.getKeyCode(),true);
    }
    public void addListener(KeyListener<Integer> listener) {
        listeners.add(listener);
    }
    public void removeListener(KeyListener<Integer> listener) {
        listeners.remove(listener);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        pressedKeys.put(nativeKeyEvent.getKeyCode(),false);
        int key = nativeKeyEvent.getKeyCode();
        Boolean cntrl = pressedKeys.get(29);
        for (KeyListener<Integer> listener : listeners) {
            listener.run(key, cntrl != null && cntrl);
        }
        Boolean shift = pressedKeys.get(42);
        //Random bugi, että potenssi nappia ei voi painaa, kun tämä kuuntelee nappeja???
        if (shift != null && shift && key==39 && !block) {
            System.out.println("Potenssi nappia painettu");
            //painaPotenssiNappia();
        }
    }
    private static boolean block = false;
    public static void painaPotenssiNappia() {
        block = true;
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_CIRCUMFLEX);
            robot.keyRelease(KeyEvent.VK_CIRCUMFLEX);
            robot.keyRelease(KeyEvent.VK_SHIFT);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                block = false;
            }
        }, 1000);
    }

}
