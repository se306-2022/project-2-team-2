package models;

import org.graphstream.graph.Node;

import java.util.*;

/**
 * State represents partial solution.
 */
public class Schedule {

    private List<Task> tasks;

    public Schedule(List<Task> tasks) {
        this.tasks = tasks;
    }

    public int getLatestFinishTime() {
        int time = 0;
        for (Task task : tasks) {
            time = Math.max(time, task.getFinishTime());
        }
        return time;
    }

    public int getEarliestFinishTime() {
        int time = (tasks.isEmpty()) ? 0 : Integer.MAX_VALUE;
        for (Task task : tasks) {
            time = Math.min(time, task.getFinishTime());
        }
        return time;
    }
}
