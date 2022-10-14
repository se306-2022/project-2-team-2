## Greedy


For Milestone 1, we originally implemented the A* Search Algorithm as we wanted to prioritise speed over memory usage.

For the **input**, we accept a `Graph` (from the Graphstream library) and also the number of processors that the schedule will be made with.

**Solution**: This algorithm schedules the highest priority free node onto the earliest available processors.
* Communication cost between processors into account if the input number of processors is greater than one. The priority is sorted using a comparator - one for node weight and the other for bottom level of each node in the graph.
*  Two comparators are both used independently and are compared to see which will produce the fastest schedule.  Once the free node has been scheduled.
* All earliest start times for each processor for each of the current node's children will be updated, and will also increment the number of parents that have been scheduled for each child.
* Earliest start times are stored in a hashmap, with the key being the key of the node (supplied by the Graphstream `Node` class) and the value being an array of integers with the length of the number of processors.

To be added in the queue, a node must be free, this means it will either have no parents, or all the parents of that node have been added to the schedule. Nodes that had no parents initially were added into the queue, when all parents of each node are checked, then the node will be added into the queue. We kept track of the number of parents that had been scheduled using a hashmap. A hashmap was chosen over an array as we wanted to allow for both numbers and nodes to be used as keys for the graph nodes.

This implementation has a few downsides; it was not an optimal solution and only has two comparators which determine the priority of the free nodes. It also quite memory intensive.

## Testing

* JUnit tests were used
* We had definitive answers for each graph, so comparison was quite simple.

After milestone one, we had decided to change to using Branch and Bound instead as it used less memory compared to A*, so we opted to use this algorithm as a hueristic instead. This would give a non infinite initial schedule as the initial best schedule. 
