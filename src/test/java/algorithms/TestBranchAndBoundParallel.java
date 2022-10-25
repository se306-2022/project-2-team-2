package algorithms;

import IO.IOParser;
import org.graphstream.graph.Graph;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestBranchAndBoundParallel {
    private Graph graph;
    private BranchAndBoundParallel branchAndBound;
    private final String GRAPH_DIR = "src/test/graphs/";

    @Test
    public void TestGraph1() {
        graph = IOParser.read(GRAPH_DIR + "Graph1.dot");
        branchAndBound = new BranchAndBoundParallel(graph, 2, 2);
        branchAndBound.run();
        assertEquals(8, branchAndBound.getFastestTime());
        assertTrue(branchAndBound.getBestSchedule().isValid());
    }

    @Test
    public void TestNodes7OutTree2Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_7_OutTree.dot");
        branchAndBound = new BranchAndBoundParallel(graph, 2, 3);
        branchAndBound.run();
        assertEquals(28, branchAndBound.getFastestTime());
        assertTrue(branchAndBound.getBestSchedule().isValid());
    }

    @Test
    public void TestNodes8Random2Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_8_Random.dot");
        branchAndBound = new BranchAndBoundParallel(graph, 2, 1);
        branchAndBound.run();
        assertEquals(581, branchAndBound.getFastestTime());
        assertTrue(branchAndBound.getBestSchedule().isValid());
    }

    @Test
    public void TestNodes9SeriesParallel2Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_9_SeriesParallel.dot");
        branchAndBound = new BranchAndBoundParallel(graph, 2, 4);
        branchAndBound.run();
        assertEquals(55, branchAndBound.getFastestTime());
        assertTrue(branchAndBound.getBestSchedule().isValid());
    }

    @Test
    public void TestNodes10Random2Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_10_Random.dot");
        branchAndBound = new BranchAndBoundParallel(graph, 2, 1);
        branchAndBound.run();
        assertEquals(50, branchAndBound.getFastestTime());
        assertTrue(branchAndBound.getBestSchedule().isValid());
    }

    @Test
    public void TestNodes11OutTree2Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_11_OutTree.dot");
        branchAndBound = new BranchAndBoundParallel(graph, 2, 2);
        branchAndBound.run();
        assertEquals(350, branchAndBound.getFastestTime());
        assertTrue(branchAndBound.getBestSchedule().isValid());
    }

    @Test
    public void TestNodes7OutTree4Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_7_OutTree.dot");
        branchAndBound = new BranchAndBoundParallel(graph, 4, 3);
        branchAndBound.run();
        assertEquals(22, branchAndBound.getFastestTime());
        assertTrue(branchAndBound.getBestSchedule().isValid());
    }

    @Test
    public void TestNodes8Random4Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_8_Random.dot");
        branchAndBound = new BranchAndBoundParallel(graph, 4, 4);
        branchAndBound.run();
        assertEquals(581, branchAndBound.getFastestTime());
        assertTrue(branchAndBound.getBestSchedule().isValid());
    }

    @Test
    public void TestNodes9SeriesParallel4Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_9_SeriesParallel.dot");
        branchAndBound = new BranchAndBoundParallel(graph, 4, 2);
        branchAndBound.run();
        assertEquals(55, branchAndBound.getFastestTime());
        assertTrue(branchAndBound.getBestSchedule().isValid());
    }

    @Test
    public void TestNodes10Random4Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_10_Random.dot");
        branchAndBound = new BranchAndBoundParallel(graph, 4, 3);
        branchAndBound.run();
        assertEquals(50, branchAndBound.getFastestTime());
        assertTrue(branchAndBound.getBestSchedule().isValid());
    }

    @Test
    public void TestNodes11OutTree4Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_11_OutTree.dot");
        branchAndBound = new BranchAndBoundParallel(graph, 4, 2);
        branchAndBound.run();
        assertEquals(227, branchAndBound.getFastestTime());
        assertTrue(branchAndBound.getBestSchedule().isValid());
    }
}
