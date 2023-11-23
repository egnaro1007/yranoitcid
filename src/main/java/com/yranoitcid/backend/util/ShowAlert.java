package com.yranoitcid.backend.util;

import com.yranoitcid.frontend.Application;
import java.security.AllPermission;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public interface ShowAlert {

    static void showInformationAlert(String title, String header, String content) {
        Alert connectionFail = getAlert(AlertType.INFORMATION, title, header, content);
        connectionFail.show();
    }

    static void showWarningAlert(String title, String header, String content) {
        Alert connectionFail = getAlert(AlertType.WARNING, title, header, content);
        connectionFail.show();
    }

    static void showConfirmationAlert(String title, String header, String content) {
        Alert connectionFail = getAlert(AlertType.CONFIRMATION, title, header, content);
        connectionFail.show();
    }

    static void showErrorAlert(String title, String header, String content) {
        Alert connectionFail = getAlert(AlertType.ERROR, title, header, content);
        connectionFail.show();
    }

    static Alert getAlert(AlertType alertType, String title, String header) {
        return getAlert(alertType, title, header, "");
    }

    static Alert getAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().getStylesheets().add(Application.styleSheets);
        alert.getDialogPane().getStyleClass().add("dialog");

        return alert;
    }
}
