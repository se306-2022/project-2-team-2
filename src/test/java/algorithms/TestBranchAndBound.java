package algorithms;

import IO.IOParser;
import org.graphstream.graph.Graph;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestBranchAndBound {

    private Graph graph;

    @Before
    public void setUp() {
        graph = IOParser.read("src/test/graphs/graph1.dot");
    }

    @Test
    public void TestBLevels() {
        BranchAndBound branchAndBound = new BranchAndBound(graph, 2);

        int[] bLevels = branchAndBound.getbLevels();

        int nodeAIndex = graph.getNode("a").getIndex();
        int nodeBIndex = graph.getNode("b").getIndex();
        int nodeCIndex = graph.getNode("c").getIndex();
        int nodeDIndex = graph.getNode("d").getIndex();

        assertEquals(7, bLevels[nodeAIndex]);
        assertEquals(5, bLevels[nodeBIndex]);
        assertEquals(5, bLevels[nodeCIndex]);
        assertEquals(2, bLevels[nodeDIndex]);
    }
}
