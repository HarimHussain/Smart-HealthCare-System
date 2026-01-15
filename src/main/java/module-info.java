module com.example.smarthealthcare {
    requires javafx.controls;
    requires javafx.fxml;

    opens ui to javafx.fxml;
    opens ui.controllers to javafx.fxml;
    opens ui.views to javafx.fxml;  // Add this line for FXML files

    exports ui;
    exports ui.controllers;
    exports users;
    exports appointments;
    exports exceptions;
    exports database;
    exports interfaces;
    exports system;
}