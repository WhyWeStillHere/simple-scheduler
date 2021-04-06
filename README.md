# Executor for tasks that dependendent from other tasks
## Classes
Realization was splitted in 3 classes
* CyclicDependenciesChecker - checks if we have cyclic dependecies in our tasks
* ExecutionQueue provide tasks that ready for execution (according to dependencies). If we wait for available tasks it blocks, if we solved all our tasks it return null
* TaskExecutor concurrently executes our tasks in order provided by execution queue
