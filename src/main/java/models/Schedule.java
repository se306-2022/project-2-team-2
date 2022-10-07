package models;

import org.graphstream.graph.Node;

import java.util.*;

/**
 * State represents partial solution.
 */
public class Schedule {

    private List<ResultTask> tasks;

    public Schedule(List<ResultTask> tasks) {
        this.tasks = tasks;
    }

    public int getLatestFinishTime() {
        int time = 0;
        for (ResultTask task : tasks) {
            time = Math.max(time, task.getFinishTime());
        }
        return time;
    }

    public int getEarliestFinishTime() {
        int time = (tasks.isEmpty()) ? 0 : Integer.MAX_VALUE;
        for (ResultTask task : tasks) {
            time = Math.min(time, task.getFinishTime());
        }
        return time;
    }

    public int getTaskStartTime(Node node) {
        for (ResultTask task : tasks) {
            if (task.getNode().getIndex() == node.getIndex()) {
                return task.getStartTime();
            }
        }

        return 0;
    }

    public List<ResultTask> getTasksList() {
        return tasks;
    }

    public void addTask(ResultTask task) {
        this.tasks.add(task);
    }
}
