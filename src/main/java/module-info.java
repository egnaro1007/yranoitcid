module com.yranoitcid{
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires java.sql;
    requires json.simple;
    requires org.xerial.sqlitejdbc;
    requires org.slf4j;
    requires javafx.base;
    requires javafx.web;

    opens com.yranoitcid.Frontend to javafx.fxml;
    exports com.yranoitcid.Frontend;
}
