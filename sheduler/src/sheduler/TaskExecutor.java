package sheduler;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskExecutor {
  public void execute(Collection<Task> tasks) {
    if (tasks == null || tasks.isEmpty()) {
      return;
    }
    CyclicDependenciesChecker checker = new CyclicDependenciesChecker();
    if (checker.findCyclicDependencies(tasks)) {
      throw new IllegalArgumentException("Found cyclic dependencies");
    }
    ExecutionQueue queue = new ExecutionQueue();
    queue.Build(tasks);

    ExecutorService service = Executors.newCachedThreadPool();
    while (true) {
      Task currentTask = queue.availableTask();
      if (currentTask == null) {
        break;
      }
      service.submit(currentTask::execute);
    }
  }
}
