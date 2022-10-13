package algorithms;

import java.util.LinkedList;
import models.ResultTask;
import models.Schedule;
import org.graphstream.graph.Graph;

public abstract class Algorithm {
    private Graph graph;
    private Schedule bestSchedule;
    private int stateCount;
    private int bestFinishTime;

    public abstract void run();

    public Schedule getBestSchedule() {
        return bestSchedule;
    }

    public int getStateCount() {
        return stateCount;
    }

    public int getBestFinishTime() {
        return bestFinishTime;
    }

    protected void updateVisualisation() {

    }

    public void setInitialSchedule(Schedule schedule) {
        this.bestFinishTime = schedule.getFinishTime();

        LinkedList<ResultTask> tasks = schedule.getTasks();
        bestSchedule = new Schedule(tasks);
    }

}
