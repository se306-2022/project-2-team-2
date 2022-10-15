package visualisation;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import models.ResultTask;
import models.Schedule;

import java.util.LinkedList;

public class Updater {

    Timeline timeline;
    private double timeElapsed = 0;
    private GanttChart currentChart;
    private GanttChart bestChart;

    
    public Updater(GanttChart currentChart, GanttChart bestChart) {

        this.currentChart = currentChart;
        this.bestChart = bestChart;
    }

    public void updateChart(GanttChart chart, Schedule schedule, int numProcessors) {
        LinkedList<ResultTask> tasks = schedule.getTasks();
        int[] startTimes = new int[tasks.size()];
        int[] processors = new int[tasks.size()];

        for (ResultTask task : tasks) {
            int i = task.getNode().getIndex();
            startTimes[i] = task.getStartTime();
            processors[i] = task.getProcessor();
        }

        // Initialize processor row of tasks
        XYChart.Series[] rows = new XYChart.Series[processors.length];
        for (int i = 0; i < processors.length; i++) {
            rows[i] = new XYChart.Series();
        }

        // iterate through tasks
        for (ResultTask task : tasks) {
            // get its id / index
            int taskId = task.getNode().getIndex();
            // get its processor (Y axis row)
            int taskProcessorId = processors[taskId];

            // If the task have not been scheduled yet, it will have a -1 mapping to the processor
            if (taskProcessorId == -1) {
                break;
            }
            // the width in terms of x-axis
            int taskWeight = task.getNode().getAttribute("Weight", Double.class).intValue();

            // x-axis intercept
            int taskStartTime = startTimes[taskId];

            // This will appear as a rectangle in a row from start time until it's start time + weight
            GanttChart.ExtraData taskData = new GanttChart.ExtraData(taskWeight, "bar");
            XYChart.Data data = new XYChart.Data(taskStartTime, "Processor " + taskProcessorId, taskData);
            rows[taskProcessorId].getData().add(data);
        }
        // remove previous graph
        chart.getData().clear();
        for (int i = 0; i < processors.length; i++) {
            NumberAxis x = (NumberAxis) chart.getXAxis();
            chart.getData().add(rows[i]);
        }
    }
}
