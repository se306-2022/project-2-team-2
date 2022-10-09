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

    @Test
    public void TestBasicGraphOutput() {
        graph = IOParser.read("src/test/graphs/graph1.dot");
        branchAndBound = new BranchAndBound(graph, 2);
        branchAndBound.run();

        Schedule bestSchedule = branchAndBound.getBestSchedule();
        for (ResultTask task : bestSchedule.getTasks()) {
            System.out.println(task.toString());
        }
    }

    @Test
    public void TestNodes7OutTree2Proc() {
        graph = IOParser.read("src/test/graphs/Nodes_7_OutTree.dot");
        branchAndBound = new BranchAndBound(graph, 2);
        branchAndBound.run();

        Schedule bestSchedule = branchAndBound.getBestSchedule();
        for (ResultTask task : bestSchedule.getTasks()) {
            System.out.println(task.toString());
        }

        System.out.println("Best time: " + branchAndBound.getFastestTime());
    }

    @Test
    public void TestNodes8Random2Proc() {
        graph = IOParser.read("src/test/graphs/Nodes_8_Random.dot");
        branchAndBound = new BranchAndBound(graph, 2);
        branchAndBound.run();

        Schedule bestSchedule = branchAndBound.getBestSchedule();
        for (ResultTask task : bestSchedule.getTasks()) {
            System.out.println(task.toString());
        }

        System.out.println("Best time: " + branchAndBound.getFastestTime());
    }

    @Test
    public void TestNodes9SeriesParallel() {
        graph = IOParser.read("src/test/graphs/Nodes_9_SeriesParallel.dot");
        branchAndBound = new BranchAndBound(graph, 2);
        branchAndBound.run();

        Schedule bestSchedule = branchAndBound.getBestSchedule();
        for (ResultTask task : bestSchedule.getTasks()) {
            System.out.println(task.toString());
        }

        System.out.println("Best time: " + branchAndBound.getFastestTime());
    }

    @Test
    public void TestNodes10Random() {
        graph = IOParser.read("src/test/graphs/Nodes_10_Random.dot");
        branchAndBound = new BranchAndBound(graph, 2);
        branchAndBound.run();

        Schedule bestSchedule = branchAndBound.getBestSchedule();
        for (ResultTask task : bestSchedule.getTasks()) {
            System.out.println(task.toString());
        }

        System.out.println("Best time: " + branchAndBound.getFastestTime());
    }

    @Test
    public void TestNodes11OutTree() {
        graph = IOParser.read("src/test/graphs/Nodes_11_OutTree.dot");
        branchAndBound = new BranchAndBound(graph, 2);
        branchAndBound.run();

        Schedule bestSchedule = branchAndBound.getBestSchedule();
        for (ResultTask task : bestSchedule.getTasks()) {
            System.out.println(task.toString());
        }

        System.out.println("Best time: " + branchAndBound.getFastestTime());
    }
}
