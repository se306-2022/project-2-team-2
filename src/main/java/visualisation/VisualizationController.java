package visualisation;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.beans.binding.Bindings;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.sun.management.OperatingSystemMXBean;
import javafx.scene.control.Label;
import javafx.scene.chart.PieChart;

public class VisualizationController {
    private static UITimer timer;

    @FXML
    private Label timerLabel;
    @FXML
    private Label statesSearchedLabel;
    @FXML
    private LineChart ramChart;
    @FXML
    private LineChart cpuChart;

    @FXML
    private PieChart statesPieChart;
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private ScheduledExecutorService scheduledExecutorService;
    final int WINDOW_SIZE = 10;

    /**
     *  Initialises components of the JavaFX window for visualisation
     */
    @FXML
    public void initialize() {
        timer = new UITimer();
        timer.setController(this);
        initialisePieChart();
    }

    /**
     *  Initialise pie chart with all not searched states and 0% label
     */
    private void initialisePieChart() {
        pieChartData.add(new PieChart.Data("Searched", 0));
        pieChartData.add(new PieChart.Data("Not Searched", 1));

        statesPieChart.setLabelsVisible(false);
        statesPieChart.setLegendVisible(false);
        statesPieChart.setData(pieChartData);

        statesSearchedLabel.setText("0%");
    }

    /**
     * Handles start action when the start button is pressed
     */
    public void startAction() {
        timer.startUITimer();
        initCPUChart();
        initRAMChart();
        setPieChart(20, 4);
    }

    /**
     * Handles stop action when the stop button is pressed
     */
    public void stopAction() {
        timer.stopUITimer();
    }

    /**
     * Sets values of pie chart and updates percentage label
     * @param totalStates
     * @param statesSearched
     */
    private void setPieChart(int totalStates, int statesSearched) {
        pieChartData.get(0).setPieValue(statesSearched);
        pieChartData.get(1).setPieValue(totalStates-statesSearched);

        String percentageLabel;
        if(totalStates != 0) {
            String percentageSearched = String.valueOf(BigDecimal.valueOf(statesSearched * 100).divide(BigDecimal.valueOf(totalStates), 30, RoundingMode.HALF_UP));
            percentageLabel = percentageSearched.substring(0, 4) + "%";
        } else {
            percentageLabel = "0%";
        }
        statesSearchedLabel.setText(percentageLabel);
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
     * Sets up chart displaying realtime RAM usage
     */
    public void initRAMChart() {

        // TODO: Needs cleaning anf refactoring. A lot of duplicate code between initRAMChart() and initCPUChart().

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        //defining the axes
        final CategoryAxis xAxis = new CategoryAxis(); // we are gonna plot against time
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time/s");
        xAxis.setAnimated(false); // axis animations are removed
        yAxis.setLabel("Value");
        yAxis.setAnimated(false); // axis animations are removed

        ramChart.setTitle("Memory Usage");
        ramChart.setAnimated(false); // disable animations

        //defining a series to display data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("RAM");

        // add series to chart
        ramChart.getData().add(series);
        ramChart.setLegendVisible(false);

        // this is used to display time in HH:mm:ss format
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        // setup a scheduled executor to periodically put data into the chart
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService.scheduleAtFixedRate(() -> {

            // Update the chart
            Platform.runLater(() -> {
                // get current time
                Date now = new Date();
                // put random number with current time
                series.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), osBean.getFreeSwapSpaceSize()));

                if (series.getData().size() > WINDOW_SIZE)
                    series.getData().remove(0);
            });
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Sets up chart displaying realtime CPU usage
     */
    public void initCPUChart() {

        // TODO: Needs cleaning and refactoring. A lot of duplicate code between initRAMChart() and initCPUChart().

        OperatingSystemMXBean osBean2 = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        //defining the axes
        final CategoryAxis xAxis = new CategoryAxis(); // we are gonna plot against time
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time/s");
        xAxis.setAnimated(false); // axis animations are removed
        yAxis.setLabel("Value");
        yAxis.setAnimated(false); // axis animations are removed

        cpuChart.setTitle("CPU Usage");
        cpuChart.setAnimated(false); // disable animations

        //defining a series to display data
        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("CPU");

        // add series to chart
        cpuChart.getData().add(series2);
        cpuChart.setLegendVisible(false);

        // this is used to display time in HH:mm:ss format
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        // setup a scheduled executor to periodically put data into the chart
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService.scheduleAtFixedRate(() -> {

            // Update the chart
            Platform.runLater(() -> {
                // get current time
                Date now = new Date();
                // put random number with current time
                series2.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), osBean2.getProcessCpuLoad()));

                if (series2.getData().size() > WINDOW_SIZE)
                    series2.getData().remove(0);
            });
        }, 0, 1, TimeUnit.SECONDS);
    }
}