module com.example.visualization {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.visualization to javafx.fxml;
    exports com.example.visualization;
}