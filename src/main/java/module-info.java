module com.utc2.domainstore {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
//    requires net.java.dev.jna;
    requires org.json;
    requires okhttp3;
    requires de.mkammerer.argon2.nolibs;
    requires mysql.connector.j;
    requires jdk.compiler;
    requires jdk.httpserver;

    opens com.utc2.domainstore to javafx.fxml;
    opens com.utc2.domainstore.controller to javafx.fxml;
    opens com.utc2.domainstore.entity.view to javafx.fxml;
    opens com.utc2.domainstore.entity.database to javafx.fxml;
    opens com.utc2.domainstore.view to javafx.fxml;

    exports com.utc2.domainstore;
    exports com.utc2.domainstore.controller;
    exports com.utc2.domainstore.entity.view;
    exports com.utc2.domainstore.entity.database;
    exports com.utc2.domainstore.view;
}
