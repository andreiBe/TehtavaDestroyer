package com.patonki;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.List;

public class Kirjoittaja {
    private Robot robot;
    public Kirjoittaja() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    private void press(int code) {
        robot.keyPress(code);
        robot.delay(100);
        robot.keyRelease(code);
    }
    private void type(String message) {
        StringSelection stringSelection = new StringSelection(message);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }
    public void teeTehtava(List<Instruction> list) {
        for (Instruction instruction : list) {
            if (!instruction.isJustText()) {
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_E);
                robot.delay(100);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyRelease(KeyEvent.VK_E);
                robot.keyPress(KeyEvent.VK_TAB);
                robot.keyRelease(KeyEvent.VK_TAB);
                robot.delay(1000);
            }
            type(instruction.getMessage());
            robot.delay(100);
            press(KeyEvent.VK_ESCAPE);
            press(KeyEvent.VK_ENTER);
        }
    }
}
