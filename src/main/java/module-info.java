module com.yranoitcid.yranoitcid {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.yranoitcid.yranoitcid to javafx.fxml;
    exports com.yranoitcid.yranoitcid;
}