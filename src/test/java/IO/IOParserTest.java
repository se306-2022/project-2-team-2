package IO;

import algorithms.BranchAndBound;
import algorithms.Greedy;
import models.ResultTask;
import models.Schedule;
import org.graphstream.graph.Graph;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class IOParserTest {

    /**
     * Test the example dot file containing the graph produces a Graph with the correct nodes and edges
     */
    @Test
    public void testFileReadInput() {
        Graph graph = IOParser.read("src/test/graphs/graph1.dot");
        assertEquals(graph.getNodeCount(), 4);
        assertEquals(graph.getNode("a").getAttribute("Weight"), 2.0);
        assertEquals(graph.getNode("b").getAttribute("Weight"), 3.0);
        assertEquals(graph.getNode("c").getAttribute("Weight"), 3.0);
        assertEquals(graph.getNode("d").getAttribute("Weight"), 2.0);
    }

    /**
     * Test that a dot file with a valid schedule is produced with branch and bound
     */
    @Test
    public void testFileWriteOutput() {
        Graph graph = IOParser.read("src/test/graphs/graph1.dot");
        BranchAndBound branchAndBound = new BranchAndBound(graph, 2);
        branchAndBound.run();
        IOParser.write("src/test/graphs/graph1Output.dot", graph, branchAndBound.getBestSchedule());
    }
}