module com.brownj.eventhandlers {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.brownj.eventhandlers to javafx.fxml;
    exports com.brownj.eventhandlers;
}