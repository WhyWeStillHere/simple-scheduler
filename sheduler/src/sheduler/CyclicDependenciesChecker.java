package sheduler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

public class CyclicDependenciesChecker {
  private enum TaskStatus {
    VISITED,
    CURRENTLY_VISITING
  }

  private final HashMap<Task, TaskStatus> taskStatuses = new HashMap<>();

  public boolean findCyclicDependencies(Collection<Task> tasks) {
    taskStatuses.clear();

    for (Task task : tasks) {
      if (findCycle(task)) {
        return true;
      }
    }
    return false;
  }

  // Not very efficient, but doesn't cause stackoverflow
  private boolean findCycle(Task startTask) {
    Stack<Task> lastTask = new Stack<>();

    lastTask.add(startTask);
    while (!lastTask.empty()) {
      boolean addedTask = false;
      Task currentTask = lastTask.peek();

      if (!taskStatuses.containsKey(currentTask)) {
        taskStatuses.put(currentTask, TaskStatus.CURRENTLY_VISITING);
      }

      for (Task task: currentTask.dependencies()) {
        if (taskStatuses.containsKey(task)) {
          if (taskStatuses.get(task) == TaskStatus.CURRENTLY_VISITING) {
            return true;
          } else {
            continue;
          }
        }
        lastTask.add(task);
        addedTask = true;
        break;
      }

      if (!addedTask) {
        lastTask.pop();
        taskStatuses.put(currentTask, TaskStatus.VISITED);
      }
    }

    return false;
  }
}