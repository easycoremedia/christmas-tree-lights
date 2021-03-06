package com.easycore.ChristmasTreeLights.helper;

import java.awt.*;
import java.util.Random;

public class Utils {
    private Utils() {
    }

    public static String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Color randomColor() {
        Random rand = new Random();

//        float r = rand.nextFloat();
//        float g = rand.nextFloat();
//        float b = rand.nextFloat();
//
//        return new Color(r,g,b);

        float h = rand.nextFloat() * 360;
        float s = 100;
        float l = 50 + rand.nextFloat() * 10;

        return Color.getHSBColor(h, s, l);
    }

    public static void fatalSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
