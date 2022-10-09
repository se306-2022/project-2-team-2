package visualisation;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.sun.management.OperatingSystemMXBean;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class VisualizationController {
    private static UITimer timer;

    @FXML
    private Label timerLabel;
    @FXML
    private Label statesSearchedLabel;
    @FXML
    private Label inputFileLabel;
    @FXML
    private Label outputFileLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Label tasksLabel;
    @FXML
    private Label processorLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label algoTypeLabel;
    @FXML
    private LineChart ramChart;
    @FXML
    private LineChart cpuChart;
    @FXML
    private PieChart statesPieChart;
    @FXML
    private BorderPane ganttPane;
    @FXML
    private HBox buttonBox;
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private OperatingSystemMXBean osInfo = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    private ScheduledExecutorService scheduledExecutorService;
    final int WINDOW_SIZE = 10;
    private GanttChart<Number, String> ganttChart;
    public static final String processorTitle = "PSR ";
    private boolean stop = true;

    /**
     *  Initialises components of the JavaFX window for visualisation
     */
    @FXML
    public void initialize() {
        timer = new UITimer();
        timer.setController(this);
        stopButton.setVisible(false);
        stopButton.setManaged(false);
        initialisePieChart();
        initCPUChart();
        initRAMChart();
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
        createGantt();
    }

    /**
     * Handles start action when the start button is pressed
     */
    public void startAction() {
        this.stop = false;
//        startButton.setDisable(true);
//        stopButton.setDisable(false);
        stopButton.setVisible(true);
        stopButton.setManaged(true);
        startButton.setVisible(false);
        startButton.setManaged(false);
        timer.startUITimer();
        setPieChart(20, 4);
        setStatusElements("parellel", "graph.dot", "outputgraph.dot", 7, 4);
    }

    /**
     * Handles stop action when the stop button is pressed
     */
    public void stopAction() {
        this.stop = true;
        stopButton.setVisible(false);
        stopButton.setManaged(false);
        startButton.setVisible(true);
        startButton.setManaged(true);
//        startButton.setDisable(false);
//        stopButton.setDisable(true);
        timer.stopUITimer();

        // Change status label text and colour
        statusLabel.setStyle("-fx-text-fill: #D70000; -fx-opacity: 60%;");
        statusLabel.setText("STOPPED");
    }

    /**
     * Set elements in status panel
     * @param inputFile
     * @param outputFile
     * @param taskCount
     * @param processorCount
     */
    private void setStatusElements(String algoType, String inputFile, String outputFile, int taskCount, int processorCount) {
        algoTypeLabel.setText(algoType.toUpperCase());
        inputFileLabel.setText(inputFile);
        outputFileLabel.setText(outputFile);
        tasksLabel.setText(taskCount + " Tasks");
        processorLabel.setText(processorCount + " Processors");
        statusLabel.setStyle("-fx-text-fill: #03ab38; -fx-opacity: 60%;");
        statusLabel.setText("RUNNING");
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
        // defining the axes and configuring graph info
        final int[] index = {-1};
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time/s");
        xAxis.setAnimated(false); // axis animations are removed
        yAxis.setLabel("Usage");
        yAxis.setAnimated(false); // axis animations are removed
        ramChart.setTitle("Memory Usage");
        ramChart.setAnimated(false); // disable animations

        //defining a series to display data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("RAM");

        // add series to chart
        ramChart.getData().add(series);
        ramChart.setLegendVisible(false);
        // setup a scheduled executor to periodically put data into the chart
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // Update the chart
            Platform.runLater(() -> {
                if (stop == false) {
                    index[0]++;
                    series.getData().add(new XYChart.Data<>(String.valueOf(index[0]), osInfo.getFreeSwapSpaceSize()));
                    if (series.getData().size() > WINDOW_SIZE)
                        series.getData().remove(0);
                    }
                });
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Sets up chart displaying realtime CPU usage
     */
    public void initCPUChart() {
        // defining the axes and configuring graph info
        final int[] index = {-1};
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time/s");
        xAxis.setAnimated(false); // axis animations are removed
        yAxis.setLabel("Usage");
        yAxis.setAnimated(false); // axis animations are removed
        cpuChart.setTitle("CPU Usage");
        cpuChart.setAnimated(false); // disable animations

        //defining a series to display data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("CPU");

        // add series to chart
        cpuChart.getData().add(series);
        cpuChart.setLegendVisible(false);

        // setup a scheduled executor to periodically put data into the chart
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // Update the chart
            Platform.runLater(() -> {
                if (stop == false) {
                    index[0]++;
                    series.getData().add(new XYChart.Data<>(String.valueOf(index[0]), osInfo.getProcessCpuLoad()));
                    if (series.getData().size() > WINDOW_SIZE)
                        series.getData().remove(0);
                    }
                });
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Creates a blank Gantt Chart
     */
    public void createGantt() {
        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();
        ArrayList<String> processorCatStr = new ArrayList<>();
        ArrayList<XYChart.Series> processorCat = new ArrayList<>();

        for (int i = 0; i < 4 ; i++) { // replace '4' with number of cores to be used
            processorCatStr.add(processorTitle + (i+1));
            processorCat.add(new XYChart.Series()); // each processor has its own series
        }

        yAxis.setCategories(FXCollections.observableArrayList(processorCatStr));
        ganttChart = new GanttChart<>(xAxis, yAxis);
        ganttPane.setCenter(ganttChart);

        //live adjust gantt chart based on window size
        ganttChart.heightProperty().addListener((observable, oldValue, newValue) -> ganttChart.setBlockHeight(newValue.doubleValue()*0.70/(4))); // replace '4' with number of cores to be used
    }

}