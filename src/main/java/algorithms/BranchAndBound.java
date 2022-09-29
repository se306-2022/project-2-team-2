package algorithms;

import models.Task;

import java.util.List;

public class BranchAndBound {

    private List<Task> tasks;
    private int processors;

    public BranchAndBound(List<Task> tasks, int processors) {
        this.tasks = tasks;
        this.processors = processors;
    }


}
