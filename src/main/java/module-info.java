module com.yranoitcid{
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires javafx.base;
    requires javafx.web;
    requires javafx.media;
    requires org.slf4j;
    requires org.json;
    requires org.jsoup;

    opens com.yranoitcid.frontend to javafx.fxml;
    exports com.yranoitcid.frontend;
}
