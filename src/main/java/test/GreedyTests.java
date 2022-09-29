package test;

import java.util.*;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Test;
import algorithms.Greedy;
import models.ResultTask;

public class GreedyTests {

    public void sequentialBFS(Graph graph) {
        int numVertices = graph.getNodeCount();
        Queue<Node> queue = new LinkedList<>();
        int finish = 0;
        ArrayList<Node> visited = new ArrayList<>();

        ResultTask[] result = new ResultTask[numVertices];
        int count = 0;

        // find all node with in degree == 0
        for (int i = 0; i < graph.getNodeCount(); i++) {
            Node node = graph.getNode(i);
            if (node.getInDegree() == 0) {
                queue.add(node);
            }
        }

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            visited.add(current);
            int cost = Integer.parseInt(current.getAttribute("cost").toString());

            int start = finish; //start of current task will be current finish time
            finish += cost; //update current finish time

            result[count] = new ResultTask(current, start, finish, 0);
            count++;

            //add all children of current node to queue if its not in queue already and also not in solution
            for (Edge edge : new Iterable<Edge>() {
                @Override
                public Iterator<Edge> iterator() {
                    return current.leavingEdges().iterator();
                }
            }) {
                //get child node from exiting edge of current node
                Node child = edge.getNode1();
                if (!visited.contains(child) && !queue.contains(child)) {
                    queue.add(child);
                }
            }
        }

        System.out.println(result);
    }

    @Test
    public void TestTopologicalSimple() {
        Graph graph = new SingleGraph("test1");
        graph.addNode("a");
        graph.getNode("a").setAttribute("cost", 2);
        graph.addNode("b");
        graph.getNode("b").setAttribute("cost", 3);
        graph.addNode("c");
        graph.getNode("c").setAttribute("cost", 3);
        graph.addNode("d");
        graph.getNode("d").setAttribute("cost", 2);

        graph.addEdge("ab", "a", "b", true);
        graph.getEdge("ab").setAttribute("communicationCost", 1);
        graph.addEdge("ac", "a", "c", true);
        graph.getEdge("ac").setAttribute("communicationCost", 2);
        graph.addEdge("bd", "b", "d", true);
        graph.getEdge("bd").setAttribute("communicationCost", 2);
        graph.addEdge("cd", "c", "d", true);
        graph.getEdge("cd").setAttribute("communicationCost", 1);
        System.out.println(graph.getNode(0));
        System.out.println(graph.getNode(1));
        System.out.println(graph.getNode(2));
        System.out.println(graph.getNode(3));

        Greedy greedy = new Greedy();

        ResultTask[] result  = greedy.GreedyScheduler(graph, 2);
        System.out.println(result);
    }

}
