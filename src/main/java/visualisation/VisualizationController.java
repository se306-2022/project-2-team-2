package visualisation;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class VisualizationController {
    private static UITimer timer;

    @FXML
    private Label timerLabel;

    /**
     *  Initialises components of the JavaFX window for visualisation
     */
    @FXML
    public void initialize() {
        timer = new UITimer();
        timer.setController(this);
    }

    /**
     * Called by {@link UITimer} startUITimer() to update timer label
     * @param counter incremented value
     */
    @FXML
    public synchronized void setUITimer(int counter) {
        Platform.runLater(() -> {
            String minEmpty = "";
            String secEmpty = "";
            String msecEmpty = "";
            // Split counter into relevant parts of timer format
            long minutes = counter / 6000;
            long seconds = (counter - minutes * 6000) / 100;
            long milliseconds = counter - minutes * 6000 - seconds * 100;

            // 0 to be appended to the front if the value is less than 10
            if (minutes < 10) {
                minEmpty = "0";
            }
            if (seconds < 10) {
                secEmpty = "0";
            }
            if (milliseconds < 10) {
                msecEmpty = "0";
            }

            String timerText = "00:" + minEmpty + minutes + ":" + secEmpty + seconds + "." + msecEmpty + milliseconds;
            timerLabel.setText(timerText);
        });
    }

    /**
     * Handles start action when the start button is pressed
     */
    public void startAction() {
        timer.startUITimer();
    }

    /**
     * Handles stop action when the stop button is pressed
     */
    public void stopAction() {
        timer.stopUITimer();
    }
}