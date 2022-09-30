package algorithms;

import java.util.*;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import models.ResultTask;

public class Greedy {
    public ResultTask[] GreedyScheduler(Graph graph, int processors) {
        int count = graph.getNodeCount();
        int finishTime = 0;
        int limit = 0;

        Queue<String> queue = new LinkedList();
        //index = node index in graph, array stores processor it is scheduled on, start and finish time (in that order)
        ResultTask[] result = new ResultTask[count];
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

            Double nodeCost = (Double) graph.getNode(current).getAttribute("Weight");
            int currFinishTime = minStart + nodeCost.intValue();
            finishTime = Math.max(finishTime, currFinishTime); //get the later finish time

            //add current node to results map
            int pos = graph.getNode(current).getIndex();
            result[pos] = new ResultTask(node, minStart, finishTime, currProcessor);

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
                                finishTime + Integer.parseInt(edge.getAttribute("Weight").toString()),
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
}
