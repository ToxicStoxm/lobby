package com.x_tornado10.lobby.utils.statics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Convertor {
    public static @NotNull String darkenHexColor(@NotNull String hexColor, double factor) {
        if (!hexColor.matches("^#([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})$")) {
            throw new IllegalArgumentException("Invalid hex color format");
        }

        Color color = Color.decode(hexColor);

        int red = (int) (color.getRed() * (1 - factor));
        int green = (int) (color.getGreen() * (1 - factor));
        int blue = (int) (color.getBlue() * (1 - factor));

        red = Math.max(0, Math.min(255, red));
        green = Math.max(0, Math.min(255, green));
        blue = Math.max(0, Math.min(255, blue));

        return String.format("#%02X%02X%02X", red, green, blue);
    }
    public static String replaceHexCodes(String input, String replacement) {
        Pattern pattern = Pattern.compile("#([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})");

        Matcher matcher = pattern.matcher(input);

        return matcher.replaceAll(replacement);
    }
    public static boolean containsHexCode(String input) {
        Pattern pattern = Pattern.compile("#([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }
    public static @Nullable String extractHexCode(String input) {
        Pattern pattern = Pattern.compile("#([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})");
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.group() : null;
    }
    public static double DEFAULT = 0.2;
    public static int TITLE_FADEIN = 20;
    public static int TITLE_STAY = 100;
    public static int TITLE_FADEOUT = 20;

}
