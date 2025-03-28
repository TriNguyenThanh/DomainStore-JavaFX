module com.utc2.domainstore {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
//    requires net.java.dev.jna;
    requires org.json;
    requires okhttp3;
    requires de.mkammerer.argon2.nolibs;

    opens com.utc2.domainstore to javafx.fxml;
    opens com.utc2.domainstore.controller to javafx.fxml;
    exports com.utc2.domainstore;
}
