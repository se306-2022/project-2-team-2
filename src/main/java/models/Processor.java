package models;

import org.graphstream.graph.Node;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Processor {

    private int id;
    private LinkedList<Task> tasks;
    private HashSet<String> scheduledTaskIds = new HashSet<>();

    public Processor(int id) {
        this.id = id;
    }

    public LinkedList<Task> getTasks() {
        return tasks;
    }

    public int getId() {
        return id;
    }

    public void addTask(Task task) {
        scheduledTaskIds.add(task.getNode().getId());
        tasks.add(task);
    }

    public void removeTask(Task task) {
        scheduledTaskIds.remove(task.getNode().getId());
        tasks.remove(task);
    }

    public int getCurrentTime() {
        if (tasks.isEmpty()) return 0;
        return tasks.getLast().getFinishTime();
    }

    public boolean containsTask(String id) {
        return scheduledTaskIds.contains(id);
    }
}
