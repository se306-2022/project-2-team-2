package models;

import org.graphstream.graph.Node;

import javax.xml.transform.Result;
import java.util.*;

/**
 * State represents partial solution.
 */
public class Schedule {

    private LinkedList<ResultTask> tasks;

    public Schedule(LinkedList<ResultTask> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Node node, int startTime, int finishTime, int processor) {
        tasks.add(new ResultTask(node, startTime, finishTime, processor));
    }

    public void popTask() {
        tasks.removeLast();
    }

    public LinkedList<ResultTask> getTasks() {
        return tasks;
    }

    public int getNumberOfScheduledTasks() {
        return tasks.size();
    }

    public int getProcessorFinishTime(int processor) {
        int processorFinishTime = 0;
        for (ResultTask task : tasks) {
            if (task.getProcessor() == processor) {
                processorFinishTime = Math.max(processorFinishTime, task.getFinishTime());
            }
        }
        return processorFinishTime;
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

    public int getTaskProcessor(Node node) {
        for (ResultTask task : tasks) {
            if (task.getNode().getIndex() == node.getIndex()) {
                return task.getProcessor();
            }
        }

        return 0;
    }
}
