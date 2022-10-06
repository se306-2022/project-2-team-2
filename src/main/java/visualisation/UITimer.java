package visualisation;

import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

public class UITimer extends Thread {
    private int counter;
    private VisualizationController _controller;
    private Timer timer;

    public void setController(VisualizationController controller) {
        _controller = controller;
    }

    /**
     * Called by {@link VisualizationController} startAction() to start timer
     */
    public void startUITimer() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                counter++;
                if (_controller != null) {
                    _controller.setUITimer(counter);
                }
            }
        };
        timer = new Timer("timer");
        timer.scheduleAtFixedRate(timerTask, 0, 10);
    }

    /**
     * Called by {@link VisualizationController} stopAction() to stop timer
     */
    public void stopUITimer() {
        timer.cancel();
    }
}
