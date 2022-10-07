package algorithms;

import IO.IOParser;
import models.ResultTask;
import models.Schedule;
import org.graphstream.graph.Graph;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestBranchAndBound {
    private Graph graph;
    private BranchAndBound branchAndBound;

    @Before
    public void setUp() {
        graph = IOParser.read("src/test/graphs/graph1.dot");
        branchAndBound = new BranchAndBound(graph, 2);
    }

    @Test
    public void TestRun() {
        branchAndBound.run();

        Schedule bestSchedule = branchAndBound.getBestSchedule();
        for (ResultTask task : bestSchedule.getTasks()) {
            System.out.println(task.toString());
        }
    }
}
