## Algorithm Paralellization

To further improve performance of the algorithm under heavly load we also parallized it's processing. Each thread would be used to process multiple search states `RecursiveWorker.class)` simultaneously. As each search state is essentially an object that kept track of fields such as the current partial schedule, the current free tasks availabe in queue, if a previous child had be added and node dependents. Each recursive iteration we would make a deep copy of the current search state so they can all be independent of each other.

## Fork Join Pool
Java provides concurrent APIs one of which is called `ForkJoinPool` which is a type of `ExecutorService` running `ForkJoinTask`s. Fork join pools are optimized for processing recursive algorithms. Fork join pools employ's work-stealing: all threads in the pool will try to find and execute tasks in the pool. 

`RecursiveWorker`s extends `RecursiveAction` class and overrides `compute()` method for processing the branch and bound algorithm. When we perform the next recrusive iteration we instead create a deep copy of the current recursive worker object with some minor changes and add it to a collection of `RecursiveWorker`s. Finally we use `invokeAll()` method which executes all the given `RecursiveWorker` tasks by adding it all to the pool. 

Because the execution of `RecursiveWorker` tasks are running concurrently we need to make sure global fields such as the current best schedule and fastest time ect. are all synchrnoized with each other. Therefore, no matter which tasks is be processed these core values are up to date. We used Java `syncrhonized {}` code blocks which notifies the monitor thread and keeps tracks of the most up to date values.

