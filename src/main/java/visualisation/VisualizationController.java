package visualisation;

import IO.IOParser;
import com.sun.management.OperatingSystemMXBean;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import models.ResultTask;
import models.Schedule;
import org.graphstream.graph.Graph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import solution.SolutionThread;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VisualizationController {

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
    private AreaChart cpuChart;
    @FXML
    private AreaChart ramChart;
    @FXML
    private PieChart statesPieChart;
    @FXML
    private BorderPane ganttPane;
    @FXML
    private  BorderPane graphPane;
    private static UITimer timer;
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private OperatingSystemMXBean osInfo = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    private ScheduledExecutorService scheduledExecutorService;
    final int WINDOW_SIZE = 10;
    private GanttChart<Number, String> ganttChart;
    private InputGraph inputGraph;
    private boolean stop = true;
    private SolutionThread solutionThread;
    private String inputFile;
    private XYChart.Series<String, Number> RAMseries;
    private XYChart.Series<String, Number> CPUseries;
    private final int[] index = {-1};


    /**
     *  Initialises components of the JavaFX window for visualisation
     */
    @FXML
    public void initialize() {
        // create new timer instance
        timer = new UITimer();
        timer.setController(this);

        // initialize start / stop button
        stopButton.setVisible(false);
        stopButton.setManaged(false);

        // initialize graphs
        createCPUChart();
        createRAMChart();
        createPieChart();
        createGanttChart();

        // ram and cpu charts start running as soon as gui opens
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // Update the chart
            Platform.runLater(() -> {
                index[0] = index[0] + 1;
                updateRAMChart();
                updateCPUChart();
            });
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    /**
     *  Initialises SolutionThread which manages the algorithm
     */
    public void setUpArgs(SolutionThread solutionThread, String inputFile) {
        this.solutionThread = solutionThread;
        this.inputFile = inputFile;
    }

    /**
     * Handles start action when the start button is pressed
     */
    public void startAction() {
        this.stop = false;

        // modify start / stop button
        stopButton.setVisible(true);
        stopButton.setManaged(true);
        startButton.setVisible(false);
        startButton.setManaged(false);

        timer.startUITimer(); // start timer

        createNodeGraph();

        // start solution thread
        solutionThread.start();
        updateGanttChart(ganttChart, solutionThread.getBestSchedule(), solutionThread.getNumProcessors());
        stop = solutionThread.getIsFinished();

        // update gantt chart periodically
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // Update the chart
            Platform.runLater(() -> {
                if (!stop) {
                    setStatusElements("parallel", this.inputFile.substring(16), "outputgraph.dot", solutionThread.getBestSchedule().getNumberOfScheduledTasks(), solutionThread.getNumProcessors());
                    updateGanttChart(ganttChart, solutionThread.getBestSchedule(), solutionThread.getNumProcessors());
                    updatePieChart((int)Math.round(Math.pow(solutionThread.getNumProcessors(), solutionThread.getBestSchedule().getNumberOfScheduledTasks())), solutionThread.getStateCount());
                    stop = solutionThread.getIsFinished();
                }

                if (stop) {
                    timer.stopUITimer();
                    stopAction();
                }

                // TODO: generate output file here.
                // TODO: change stop button to start / reset hard.
            });
        }, 0, 10, TimeUnit.MILLISECONDS);
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
        timer.stopUITimer();

        // Change status label text and colour
        statusLabel.setStyle("-fx-text-fill: #d70000; -fx-opacity: 60%;");
        statusLabel.setText("STOPPED");
    }

    /**
     * Set elements in status panel
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
     * Called by {@link UITimer} startUITimer() to update timer label
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
     * Creates a blank RAM chart
     */
    public void createRAMChart() {
        // defining the axes and configuring graph info
        this.RAMseries = new XYChart.Series<>();
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setAnimated(false); // axis animations are removed
        yAxis.setAnimated(false); // axis animations are removed
        ramChart.setAnimated(false); // disable animations

        RAMseries.setName("RAM");
        ramChart.getData().add(RAMseries);
        ramChart.setLegendVisible(false);
        ramChart.setCreateSymbols(false);
        ramChart.setStyle(".chart-series-area-line2");
    }

    /**
     * Updates RAM chart using realtime RAM usage stats
     */
    public void updateRAMChart() {
        RAMseries.getData().add(new XYChart.Data<>(String.valueOf(index[0]), osInfo.getFreeSwapSpaceSize()/1000000));
        if (RAMseries.getData().size() > WINDOW_SIZE) {
            RAMseries.getData().remove(0);
        }
    }

    /**
     * Creates a blank CPU chart
     */
    public void createCPUChart() {
        // defining the axes and configuring graph info
        this.CPUseries = new XYChart.Series<>();
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setAnimated(false); // axis animations are removed
        yAxis.setAnimated(false); // axis animations are removed
        cpuChart.setAnimated(false); // disable animations

        CPUseries.setName("CPU");
        cpuChart.getData().add(CPUseries);
        cpuChart.setLegendVisible(false);
        cpuChart.setCreateSymbols(false);
    }

    /**
     * Sets up default CPU chart
     */
    public void updateCPUChart() {
        CPUseries.getData().add(new XYChart.Data<>(String.valueOf(index[0]), osInfo.getProcessCpuLoad() * 100));
        if (CPUseries.getData().size() > WINDOW_SIZE) {
            CPUseries.getData().remove(0);
        }
    }

    /**
     *  Initialise pie chart with all not searched states and 0% label
     */
    private void createPieChart() {
        pieChartData.add(new PieChart.Data("Searched", 0));
        pieChartData.add(new PieChart.Data("Not Searched", 1));

        statesPieChart.setLabelsVisible(false);
        statesPieChart.setLegendVisible(false);
        statesPieChart.setData(pieChartData);

        statesSearchedLabel.setText("0%");
    }

    /**
     * Sets values of pie chart and updates percentage label
     */
    private void updatePieChart(int totalStates, int statesSearched) {
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
     * Creates a blank Gantt Chart
     */
    public void createGanttChart() {
        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();
        xAxis.setAnimated(false); // axis animations are removed
        yAxis.setAnimated(false); // axis animations are removed

        ganttChart = new GanttChart<>(xAxis, yAxis);
        ganttPane.setCenter(ganttChart);
        ganttChart.setLegendVisible(false);

        //live adjust gantt chart based on window size
        ganttChart.heightProperty().addListener((observable, oldValue, newValue) -> ganttChart.setBlockHeight(newValue.doubleValue()*0.70/(4))); // replace '4' with number of cores to be used
    }

    /**
     * Creates a node graph using the input graph file and graphstream library
     */
    public void createNodeGraph() {
        // graph to test with
        Graph graph = IOParser.read(this.inputFile);
        System.setProperty("org.graphstream.ui", "javafx");

        inputGraph = new InputGraph(graph, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        inputGraph.enableAutoLayout();

        FxViewPanel viewPanel = (FxViewPanel) inputGraph.addDefaultView(false);
        graphPane.setCenter(viewPanel);
    }

    /**
     * Update gantt chant values using updated schedule from solution thread
     */
    public void updateGanttChart(GanttChart chart, Schedule schedule, int numProcessors) {
        // Initialize processor row of tasks
        XYChart.Series[] rows = new XYChart.Series[numProcessors];
        for (int i = 0; i < numProcessors; i++) {
            rows[i] = new XYChart.Series();
        }

        // iterate through tasks
        for (ResultTask task : schedule.getTasks()) {
            // get its processor (Y axis row)
            int taskProcessorId = task.getProcessor();

            // the width in terms of x-axis
            int taskWeight = task.getNode().getAttribute("Weight", Double.class).intValue();

            // x-axis intercept
            int taskStartTime = task.getStartTime();

            // This will appear as a rectangle in a row from start time until it's start time + weight
            GanttChart.ExtraData taskData = new GanttChart.ExtraData(taskWeight, "bar");
            XYChart.Data data = new XYChart.Data(taskStartTime, "Processor " + taskProcessorId, taskData);
            rows[taskProcessorId].getData().add(data);
        }

        // remove previous graph
        chart.getData().clear();
        for (int i = 0; i < numProcessors; i++) {
            NumberAxis x = (NumberAxis) chart.getXAxis();
            chart.getData().add(rows[i]);
        }
    }
}