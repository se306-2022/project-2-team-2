package utils;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.stream.Collectors;

public class GraphUtils {

    /**
     * Checks if two nodes are equivalent.
     * Criteria: same number of in/out degrees, same parent/child nodes and same communication costs.
     * @param graph GraphStream graph object.
     * @param nodeA index of first node we want to compare.
     * @param nodeB index of second node we want to compare.
     * @return true/false if nodes are equivalent or not.
     */
    public static boolean isNodeEquivalent(Graph graph, int nodeA, int nodeB) {
        Node a = graph.getNode(nodeA);
        Node b = graph.getNode(nodeB);

        if (a.getInDegree() != b.getInDegree() || a.getOutDegree() != b.getOutDegree()) return false;

        List<Edge> aChildEdges = a.leavingEdges().collect(Collectors.toList());
        List<Edge> bChildEdges = b.leavingEdges().collect(Collectors.toList());
        List<Edge> aParentEdges = a.enteringEdges().collect(Collectors.toList());
        List<Edge> bParentEdges = b.enteringEdges().collect(Collectors.toList());

        aChildEdges.sort(Comparator.comparingInt(e -> e.getNode1().getIndex()));
        bChildEdges.sort(Comparator.comparingInt(e -> e.getNode1().getIndex()));
        for (int i = 0; i < aChildEdges.size(); i++) {
            int aChild = aChildEdges.get(i).getNode1().getIndex();
            int bChild = bChildEdges.get(i).getNode1().getIndex();

            int aChildCommunicationCost = aChildEdges.get(i).getAttribute("Weight", Double.class).intValue();
            int bChildCommunicationCost = bChildEdges.get(i).getAttribute("Weight", Double.class).intValue();
            if (aChild != bChild || aChildCommunicationCost != bChildCommunicationCost) return false;
        }

        aParentEdges.sort(Comparator.comparingInt(e -> e.getNode0().getIndex()));
        bParentEdges.sort(Comparator.comparingInt(e -> e.getNode0().getIndex()));
        for (int i = 0; i < aParentEdges.size(); i++) {
            int aParent = aParentEdges.get(i).getNode0().getIndex();
            int bParent = bParentEdges.get(i).getNode0().getIndex();

            int aParentCommunicationCost = aParentEdges.get(i).getAttribute("Weight", Double.class).intValue();
            int bParentCommunicationCost = bParentEdges.get(i).getAttribute("Weight", Double.class).intValue();
            if (aParent != bParent || aParentCommunicationCost != bParentCommunicationCost) return false;
        }

        return true;
    }

    /**
     * Creates list of equivalent tasks.
     * @param graph GraphStream graph object.
     * @return list of equivalent tasks indexed by task/node index.
     */
    public static List<List<Integer>> getEquivalentTasksList(Graph graph) {
        List<List<Integer>> equivalentTasksList = new ArrayList<>();
        for (int i = 0; i < graph.getNodeCount(); i++) equivalentTasksList.add(new ArrayList<>());


        HashSet<Integer> visited = new HashSet<>();

        for (int i = 0; i < graph.getNodeCount(); i++) {
            for (int j = 0; j < graph.getNodeCount(); j++) {
                if (visited.contains(j)) continue;
                if (isNodeEquivalent(graph, i, j)) {
                    equivalentTasksList.get(i).add(j);
                    equivalentTasksList.get(j).add(i);
                    visited.add(j);
                }
            }
        }

        return equivalentTasksList;
    }

    /**
     * Gets initial set of free tasks, given task has in-degree of 0.
     * @param graph GraphStream graph object.
     * @return queue of free tasks.
     */
    public static LinkedList<Integer> getInitialFreeTasks(Graph graph) {
        LinkedList<Integer> freeTasks = new LinkedList<>();
        for (int i = 0; i < graph.getNodeCount(); i++) {
            if (graph.getNode(i).getInDegree() == 0) {
                freeTasks.add(i);
            }
        }
        return freeTasks;
    }

    /**
     * Calculates the number of dependents each node has.
     *
     * @param graph GraphStream graph object.
     * @return int array of dependents count indexed by node.
     */
    public static int[] calculateDependents(Graph graph) {
        int[] dependents = new int[graph.getNodeCount()];
        for (int i = 0; i < graph.getNodeCount(); i++) {
            dependents[i] = graph.getNode(i).getInDegree();
        }
        return dependents;
    }

    /**
     * Returns array of bLevels indexed by node.
     * bLevels are calculated by summing computation costs to exit node and taking the maximum.
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
