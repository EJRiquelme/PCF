module pcf_client.pcf_client_intellij2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    //requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.datatransfer;
    requires java.logging;
    //requires java.desktop;

    opens pcf_client.executables to javafx.fxml;
    exports pcf_client.executables;
    exports controllers_general;
    opens controllers_general to javafx.fxml;
    exports controllers_virtual_class;
    opens controllers_virtual_class to javafx.fxml;
}