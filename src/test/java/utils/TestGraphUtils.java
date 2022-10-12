package utils;

import IO.IOParser;
import org.graphstream.graph.Graph;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class TestGraphUtils {
    private Graph graph;
    private int nodeAIndex;
    private int nodeBIndex;
    private int nodeCIndex;
    private int nodeDIndex;

    @Before
    public void setUp() {
        graph = IOParser.read("src/test/graphs/graph1.dot");
        nodeAIndex = graph.getNode("a").getIndex();
        nodeBIndex = graph.getNode("b").getIndex();
        nodeCIndex = graph.getNode("c").getIndex();
        nodeDIndex = graph.getNode("d").getIndex();
    }

    @Test
    public void TestCalculateBLevels() {
        int[] bLevels = GraphUtils.calculateBLevels(graph);

        assertEquals(7, bLevels[nodeAIndex]);
        assertEquals(5, bLevels[nodeBIndex]);
        assertEquals(5, bLevels[nodeCIndex]);
        assertEquals(2, bLevels[nodeDIndex]);
    }

    @Test
    public void TestCalculateDependents() {
        int[] dependents = GraphUtils.calculateDependents(graph);

        assertEquals(0, dependents[nodeAIndex]);
        assertEquals(1, dependents[nodeBIndex]);
        assertEquals(1, dependents[nodeCIndex]);
        assertEquals(2, dependents[nodeDIndex]);
    }

    @Test
    public void TestGetInitialFreeTasks() {
        LinkedList<Integer> freeTasks = GraphUtils.getInitialFreeTasks(graph);

        assertEquals(1, freeTasks.size());
        assertEquals("a", graph.getNode(freeTasks.getFirst()).getId());
    }
}
