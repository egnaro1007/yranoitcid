module com.yranoitcid.yranoitcid {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires json.simple;

    opens com.yranoitcid.yranoitcid to javafx.fxml;
    exports com.yranoitcid.yranoitcid;
}
