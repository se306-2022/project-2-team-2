package models;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import javax.xml.transform.Result;
import java.util.*;
import java.util.stream.Collectors;

/**
 * State represents partial solution.
 */
public class Schedule {

    private final LinkedList<ResultTask> tasks;

    public Schedule(LinkedList<ResultTask> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Node node, int startTime, int finishTime, int processor) {
        tasks.add(new ResultTask(node, startTime, finishTime, processor));
    }

    public int getLastProcessor() {
        return (tasks.isEmpty()) ? -1 : tasks.getLast().getProcessor();
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

    public int getFinishTime() {
        int time = 0;
        for (ResultTask task : tasks) {
            time = Math.max(time, task.getFinishTime());
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

    /**
     * Method checks if the schedule is valid. It does this by checking if any task starting time,
     * is less than it's parents finish time (+communication cost if different processor).
     * @return true / false.
     */
    public boolean isValid() {
        for (ResultTask task : tasks) {
            List<Edge> parentEdges = task.getNode().enteringEdges().collect(Collectors.toList());
            for (Edge parentEdge : parentEdges) {
                Node parent = parentEdge.getNode0();
                int communicationCost = parentEdge.getAttribute("Weight", Double.class).intValue();

                for (ResultTask parentTask : tasks) {
                    if (parentTask.getNode().getIndex() == parent.getIndex()) {
                        if (parentTask.getProcessor() == task.getProcessor() && task.getStartTime() < parentTask.getFinishTime()) return false;
                        if (parentTask.getProcessor() != task.getProcessor() && task.getStartTime() < parentTask.getFinishTime() + communicationCost) return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return tasks.hashCode();
    }
}
