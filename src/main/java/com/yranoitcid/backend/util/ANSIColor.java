package com.yranoitcid.backend.util;

public enum ANSIColor {
    // Text color (foreground)
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    MAGENTA("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),

    // Background color
    BG_BLACK("\u001B[40m"),
    BG_RED("\u001B[41m"),
    BG_GREEN("\u001B[42m"),
    BG_YELLOW("\u001B[43m"),
    BG_BLUE("\u001B[44m"),
    BG_MAGENTA("\u001B[45m"),
    BG_CYAN("\u001B[46m"),
    BG_WHITE("\u001B[47m"),

    // Text styles
    BOLD("\u001B[1m"),
    ITALIC("\u001B[3m"),  // May not be widely supported
    UNDERLINE("\u001B[4m"),
    BLINK("\u001B[5m"),    // May not be widely supported
    INVERSE("\u001B[7m"),

    // Reset
    RESET("\u001B[0m");

    private final String code;

    ANSIColor(String code) {
        this.code = code;
    }

    /**
     * Returns the name of this enum constant, as contained in the declaration.  This method may be
     * overridden, though it typically isn't necessary or desirable.  An enum class should override
     * this method when a more "programmer-friendly" string form exists.
     *
     * @return the code of color
     */
    @Override
    public String toString() {
        return code;
    }

    /**
     * System.out.println(String) with color.
     *
     * @param text  The text to be printed.
     * @param color The color of the text.
     */
    public static void consoleColorPrint(String text, ANSIColor color) {
        System.out.println(color + text + RESET);
    }
}
