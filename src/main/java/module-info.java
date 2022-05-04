module com.example.saper {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.saper to javafx.fxml;
    exports com.example.saper;
    exports com.example.saper.gamefield;
    opens com.example.saper.gamefield to javafx.fxml;
}