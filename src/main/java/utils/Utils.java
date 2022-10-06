package utils;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    /**
     * Returns array of bLevels indexed by node.
     * bLevels are calculated by summing computation costs to exit node and taking the maximum.
     *
     * @param graph GraphStream graph object.
     * @return int[] array of bLevels index by node.
     */
    public static int[] calculateBLevels(Graph graph) {
        int numTasks = graph.getNodeCount();
        int[] bLevels = new int[numTasks];

        for (int node = 0; node < numTasks; node++) {
            bBLevelsDFS(graph, bLevels, node);
        }

        return bLevels;
    }

    private static int bBLevelsDFS(Graph graph, int[] bLevels, int node) {
        if (bLevels[node] != 0) {
            return bLevels[node];
        }

        // If there are no child nodes, we are at an exit task and bLevel is its own weight.
        List<Edge> outEdges = graph.getNode(node).leavingEdges().collect(Collectors.toList());
        if (outEdges.isEmpty()) {
            bLevels[node] = graph.getNode(node).getAttribute("Weight", Double.class).intValue();
            return bLevels[node];
        }

        int maxLength = 0;
        for (Edge edge : outEdges) {
            Node childNode = edge.getNode1();
            int childNodeBLevel = bBLevelsDFS(graph, bLevels, childNode.getIndex());
            maxLength = Math.max(maxLength, childNodeBLevel);
        }

        bLevels[node] = maxLength + graph.getNode(node).getAttribute("Weight", Double.class).intValue();
        return bLevels[node];
    }
}
