package algorithms;

import IO.IOParser;
import org.graphstream.graph.Graph;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGreedy {
    private Graph graph;
    private Greedy greedy;
    private final String GRAPH_DIR = "src/test/graphs/";

    @Test
    public void TestGraph1() {
        graph = IOParser.read(GRAPH_DIR + "Graph1.dot");
        greedy = new Greedy(graph, 2);
        greedy.run();
        assertEquals(9, greedy.getBestFinishTime());
    }

    @Test
    public void TestNodes7OutTree2Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_7_OutTree.dot");
        greedy = new Greedy(graph, 2);
        greedy.run();
        assertEquals(34, greedy.getBestFinishTime());
    }

    @Test
    public void TestNodes8Random2Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_8_Random.dot");
        greedy = new Greedy(graph, 2);
        greedy.run();
        assertEquals(677, greedy.getBestFinishTime());
    }

    @Test
    public void TestNodes9SeriesParallel2Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_9_SeriesParallel.dot");
        greedy = new Greedy(graph, 2);
        greedy.run();
        assertEquals(55, greedy.getBestFinishTime());
    }

    @Test
    public void TestNodes10Random2Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_10_Random.dot");
        greedy = new Greedy(graph, 2);
        greedy.run();
        assertEquals(84, greedy.getBestFinishTime());
    }

    @Test
    public void TestNodes11OutTree2Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_11_OutTree.dot");
        greedy = new Greedy(graph, 2);
        greedy.run();
        assertEquals(410, greedy.getBestFinishTime());
    }

    @Test
    public void TestNodes7OutTree4Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_7_OutTree.dot");
        greedy = new Greedy(graph, 4);
        greedy.run();
        assertEquals(34, greedy.getBestFinishTime());
    }

    @Test
    public void TestNodes8Random4Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_8_Random.dot");
        greedy = new Greedy(graph, 4);
        greedy.run();
        assertEquals(598, greedy.getBestFinishTime());
    }

    @Test
    public void TestNodes9SeriesParallel4Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_9_SeriesParallel.dot");
        greedy = new Greedy(graph, 4);
        greedy.run();
        assertEquals(55, greedy.getBestFinishTime());
    }

    @Test
    public void TestNodes10Random4Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_10_Random.dot");
        greedy = new Greedy(graph, 4);
        greedy.run();
        assertEquals(84, greedy.getBestFinishTime());
    }

    @Test
    public void TestNodes11OutTree4Proc() {
        graph = IOParser.read(GRAPH_DIR + "Nodes_11_OutTree.dot");
        greedy = new Greedy(graph, 4);
        greedy.run();
        assertEquals(290, greedy.getBestFinishTime());
    }
}
