module com.yranoitcid{
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires java.sql;
    requires json.simple;

    opens com.yranoitcid.Frontend to javafx.fxml;
    exports com.yranoitcid.Frontend;
}
