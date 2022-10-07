package algorithms;

import java.util.*;
import models.Schedule;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import models.ResultTask;
import utils.Utils;

public class Greedy {
    private Schedule bestSchedule;
    private Schedule currentSchedule;
    private Graph graph;
    private Comparator<Integer>[] comparators;
    private int processors;

    public Greedy(Graph graph, int processors) {
        this.graph = graph;
        this.processors = processors;
    }

    public void setComparators() {
        comparators = new Comparator<>[]{new WeightComparator(), new BottomLevelComparator()};
    }

    public Schedule run() {
        setComparators();
        for (int i = 0; i < 2; i++) {
            this.currentSchedule = new Schedule(new ArrayList<>());
            GreedyScheduler(comparators[i]);
        }

        return bestSchedule;
    }

    public void GreedyScheduler(Comparator heuristic) {
        int count = graph.getNodeCount();
        int finishTime = 0;
        int limit = 0;

        PriorityQueue<Integer> queue = new PriorityQueue<>(heuristic);
        HashMap<Integer, Integer> parents = new HashMap<>();

        //intialise parents maps
        for (int i = 0; i < count; i++) {
            parents.put(i, 0);
        }

        //store start time of each task on each specific processor
        //Key is String id of node, array[j] for key = i is the min start time for task i on processor j to start
        HashMap<Integer, int[]> minStartTimesOnProcessors = new HashMap<>();

        //intialise min start map
        for (int i = 0; i < count; i++) {
            int[] value = new int[processors];
            minStartTimesOnProcessors.put(i, value);
        }

        // find all nodes with in degree of 0
        for (int i = 0; i < count; i++) {
            Node node = graph.getNode(i);
            if (node.getInDegree() == limit) {
                queue.add(node.getIndex());
            }
        }

        while (!queue.isEmpty()) {
            int current = queue.poll();
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
            currentSchedule.addTask(new ResultTask(node, minStart, finishTime, currProcessor));

            // get children of current node and iterate through
            for (Edge edge : new Iterable<Edge>() {
                @Override
                public Iterator<Edge> iterator() {
                    return node.leavingEdges().iterator();
                }
            }) {
                //get child node from current edge of current node
                Node child = edge.getNode1();
                int childId = child.getIndex();

                //update the min start time for child node on each processor
                for (int i = 0; i < processors; i++) {
                    if (i == currProcessor) {
                        minStartTimesOnProcessors.get(childId)[currProcessor] = Math.max(finishTime, minStartTimesOnProcessors.get(childId)[currProcessor]);
                    } else {
                        Double communicationCost = (Double) edge.getAttribute("Weight");
                        minStartTimesOnProcessors.get(childId)[i] = Math.max(finishTime + communicationCost.intValue(), minStartTimesOnProcessors.get(childId)[i]);
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
                minStartTimesOnProcessors.get(i)[currProcessor] = Math.max(finishTime, minStartTimesOnProcessors.get(i)[currProcessor]);
            }
        }

        //compare current schedule and replace best with current if the final finish time is shorter
        if (finishTime < bestSchedule.getEarliestFinishTime()) {
            bestSchedule = currentSchedule;
        }

    }

    private class BottomLevelComparator implements Comparator<Integer> {
        int[] bottomLevels = Utils.calculateBLevels(graph);
        @Override
        public int compare(Integer o1, Integer o2) {
            int taskA = bottomLevels[o1];
            int taskB = bottomLevels[o2];

            int compare = Integer.compare(taskA, taskB);
            if (compare != 0) {
                return compare;
            }
            //use index as comparison
            return Integer.compare(o1, o2);
        }
    }

    private class WeightComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            Node taskA = graph.getNode(o1);
            Node taskB = graph.getNode(o2);

            int compare = Double.
                    compare((Double) taskA.getAttribute("Weight"), (Double) taskB.getAttribute("Weight"));

            if (compare != 0) {
                return compare;
            }
            //use index as comparison
            return Integer.compare(o1, o2);
        }
    }
}
