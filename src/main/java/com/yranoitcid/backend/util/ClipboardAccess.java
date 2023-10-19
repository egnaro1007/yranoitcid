package com.yranoitcid.backend.util;

import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

public class ClipboardAccess {
    public static String getString() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        return clipboard.getContent(DataFormat.PLAIN_TEXT).toString();
    }
}
