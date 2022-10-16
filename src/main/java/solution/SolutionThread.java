package solution;

import algorithms.Algorithm;
import models.Schedule;

public class SolutionThread extends Thread {
    protected int processors;
    protected Algorithm solution;
    protected boolean finished = false;

    /**
     *
     * @param solution chosen algorithm which will run this thread
     * @param processors number of processors specified to schedule tasks on
     */
    public SolutionThread(Algorithm solution, int processors) {
        super();
        this.solution = solution;
        this.processors = processors;
    }

    @Override
    public void run() {
        solution.run();
        finished = true;
    }

    public int getStateCount() {
        return solution.getStatesSearched();
    }

    public int getCurrentBest() {
        return solution.getBestFinishTime();
    }

    public int getNumProcessors() {
        return this.processors;
    }

    public Schedule getBestSchedule() {
        return solution.getBestSchedule();
    }

    public boolean getIsFinished() {return finished;}
}
