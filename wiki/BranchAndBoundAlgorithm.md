## DFS Branch & Bound

We decided to use a DFS branch and bound approach algorithhm to solve the optimal task scheduling problem. We wanted a recursive approach with the use of backtracking to minimize memory footprint. Branch and bound in combination with multiple optimization and pruning techniques significantly reduced our search space. 

## Optimizations

Our optimization techninques consisted of two parts: the initial setup, followed by pruning techniques.

### Setup
- Blevels calculations for each node. Bottom level (computation) is the longest path to the exit task for each given node. This metric was signnificant in helping the DFS algorithm deside which free task to visit next as it was always sorted in bLevel order.
- Dependents of each node was calulated and stored in an array indexed by node index. The dependents is a measure of how many parent nodes a task is currently dependent on. This helped in determining if a task could be added to free task queue and helped with backtracking.
- List of equivalent tasks. Before starting we prepared an array of equivalent tasks. This ensured that tasks with the same parents and children and same communication cost as another node. These were quite rare, but it helped in reducing the number of states searched down a little.
- Fastest time was calculated using a greedy algorithm initially. This helped get us an initial upperbound very quickly helping use reduce the search space later on.

### Pruning
- The first pruning technique was to check if we have visited and equivalent partial schedule with the same order of tasks. If we have we can safely backtrack and look for anonther schedule. Otherwise, we hashsify the current schedule object and store it in a hashmap. 
- Another pruning technique was to check if we have already attempted to schedule an similar or equivalent task. This was tracked using a hashset. All equivalent tasks calculated from before would be added to this if a node had any.
- The third technique was to normalize the processors. We consider two processors to be isomorphic if they are both empty and waiting to recieve a task. Scheduling a task on multiple isomorphic processors would be redundant as they produce similar results. We checked if a processor has a finish time of 0, meaning it hasn't be utilized yet. If a previous processor was also the same (isomorphic) then we will skip. 
- Finally the most effective pruning technique was to check if the current task start time + the bLevel of the task exceeded the fastest finish time. If this was the case then we can skip it entirely. 


