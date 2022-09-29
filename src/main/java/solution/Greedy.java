package solution;

import java.lang.reflect.Array;
import java.util.*;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class Greedy {
    public Node GreedySchedule(Graph graph, int processors) {
        PriorityQueue<Node> open = new PriorityQueue<>();
        PriorityQueue<Node> closed = new PriorityQueue<>();
        Queue<String> queue = new LinkedList<>();
        // find start node(s) (node with no in degree) and add to queue
        for (int i = 0; i < graph.getNodeCount(); i++) {
            Node node = graph.getNode(i);
            if (node.getInDegree() == 0) {
                queue.add(node.getId());
            }
        }

        //find target node (node with no out edges )

        while (!open.isEmpty()) {
            Node currentNode = open.peek();
//            if (currentNode.equals(target)) return currentNode;

//            for (Node node : new Iterable<Node>() {
//                @Override
//                public Iterator<Edge> iterator() {
//                    return currentNode.leavingEdges().iterator();
//                }
//            }) {
//                Edge currEdge = node.getEdgeBetween(currentNode);
//                double totalWeight = Double.parseDouble(node.getAttribute("weight").toString())
//                                + Double.parseDouble(currEdge.getAttribute("weight").toString());
//                if (!open.contains(node) && !closed.contains(node)) {
//
//                } else {
//
//                }
//            }

        }

        return null;
    }

    public Task[] GreedyScheduler(Graph graph, int processors) {
        int count = graph.getNodeCount();
        int finishTime = 0;
        int limit = 0;

        Queue<String> queue = new LinkedList();
        //index = node index in graph, array stores processor it is scheduled on, start and finish time (in that order)
        Task[] result = new Task[count];
        HashMap<String, Integer> parents = new HashMap<>();

        //intialise parents maps
        for (int i = 0; i < count; i++) {
            parents.put(graph.getNode(i).getId(), 0);
        }

        //store start time of each task on each specific processor
        //Key is String id of node, array[j] for key = i is the min start time for task i on processor j to start
        HashMap<String, int[]> minStartTimesOnProcessors = new HashMap<>();

        //intialise min start map
        for (int i = 0; i < count; i++) {
            int[] value = new int[processors];
            minStartTimesOnProcessors.put(graph.getNode(i).getId(), value);
        }

        Node[] output = new Node[count];

        // find all nodes with in degree of 0
        for (int i = 0; i < count; i++) {
            Node node = graph.getNode(i);
            if (node.getInDegree() == limit) {
                queue.add(node.getId());
            }
        }

        while (!queue.isEmpty()) {
            String current = queue.poll();
            Node node = graph.getNode(current);

            int currProcessor = 0;
            int minStart = minStartTimesOnProcessors.get(current)[0];

            //find processor for the task that has the min start time
            for (int i = 1; i < processors; i++) {
                int currentTime = minStartTimesOnProcessors.get(current)[i];
                if (currentTime < minStart) {
                    minStart = currentTime;
                    currProcessor = i;
                }
            }

            int currFinishTime = minStart + Integer.parseInt(graph.getNode(current).getAttribute("cost").toString());
            finishTime = Math.max(finishTime, currFinishTime); //get the later finish time

            //add current node to results map
            int pos = graph.getNode(current).getIndex();
            result[pos] = new Task(node, minStart, finishTime, currProcessor);

            // get children of current node and iterate through
            for (Edge edge : new Iterable<Edge>() {
                @Override
                public Iterator<Edge> iterator() {
                    return node.leavingEdges().iterator();
                }
            }) {
                //get child node from current edge of current node
                Node child = edge.getNode1();
                String childId = child.getId();

                //update the min start time for child node on each processor
                for (int i = 0 ; i < processors; i++) {
                    if (i == currProcessor) {
                        minStartTimesOnProcessors.get(childId)[currProcessor] =
                                Math.max(finishTime, minStartTimesOnProcessors.get(childId)[currProcessor]);
                    } else {
                        minStartTimesOnProcessors.get(childId)[i] = Math.max(
                                finishTime + Integer.parseInt(edge.getAttribute("communicationCost").toString()),
                                minStartTimesOnProcessors.get(childId)[i]);
                    }
                }

                //increment parents value, if all parents have been visited for current child node, add to queue
                int visited = parents.get(childId);
                visited++;
                parents.put(childId, visited);
                if (child.getInDegree() == visited) {
                    queue.add(childId);
                }
            }

            for (int i = 0; i < count; i++) {
                String id = graph.getNode(i).getId();
                minStartTimesOnProcessors.get(id)[currProcessor] =
                        Math.max(finishTime, minStartTimesOnProcessors.get(id)[currProcessor]);
            }
        }

        return result;

    }

    private double calculateLoadBalancingBound(Node node) {


        return -1;
    }

    private double calculateBottomLevelBound(Graph graph, Node node) {
        return -1;
    }
}
