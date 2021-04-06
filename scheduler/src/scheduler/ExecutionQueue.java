package scheduler;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutionQueue {
  private final BlockingQueue<ConcurrentTask> availableTasks = new LinkedBlockingQueue<>();
  private ConcurrentTask finalTask;

  public void Build(Collection<Task> tasks) {
    List<ConcurrentTask> concurrentTasks = new ArrayList<>();
    HashMap<Task, ConcurrentTask> taskMatching = new HashMap<>();
    EmptyTask finalEmptyTask = new EmptyTask();

    for (Task task: tasks) {
      ConcurrentTask concurrentTask = new ConcurrentTask(this, task);
      taskMatching.put(task, concurrentTask);
      concurrentTasks.add(concurrentTask);
    }

    for (ConcurrentTask task: concurrentTasks) {
      for (Task dependencyTask : task.dependencies()) {
        taskMatching.get(dependencyTask).addDependentTask(task);
      }
    }

    for (ConcurrentTask task: concurrentTasks) {
      if (task.getDependentTasks().isEmpty()) {
        finalEmptyTask.addDependency(task);
      }
      if (task.dependencies().isEmpty()) {
        availableTasks.add(task);
      }
    }

    finalTask = new ConcurrentTask(this, finalEmptyTask);
    for (ConcurrentTask task: concurrentTasks) {
      if (task.getDependentTasks().isEmpty()) {
        task.addDependentTask(finalTask);
      }
    }
  }
  public Task availableTask() {
    Task task;

    try {
      task = availableTasks.take();
    } catch (InterruptedException e) {
      return null;
    }

    if (task.equals(finalTask)) {
      availableTasks.add(finalTask);
      return null;
    }
    return task;
  }

  private void addTask(ConcurrentTask concurrentTask) {
    try {
      availableTasks.put(concurrentTask);
    } catch (InterruptedException e) {
      throw new RuntimeException("Thread was interrupted");
    }
  }

  private static class ConcurrentTask implements Task {
    private final Task task;
    private final AtomicInteger unsolvedDependenciesCount;
    private final List<ConcurrentTask> dependentTasks = new ArrayList<>();
    private final ExecutionQueue executionQueue;

    public ConcurrentTask(ExecutionQueue executionQueue, Task task) {
      this.executionQueue = executionQueue;
      this.task = task;
      unsolvedDependenciesCount = new AtomicInteger(task.dependencies().size());
    }

    @Override
    public void execute() {
      try {
        task.execute();
      } catch (Exception e) {
        // Error in task runtime
      }

      for (ConcurrentTask dependentTask: dependentTasks) {
        if (dependentTask.solvedDependency()) {
          executionQueue.addTask(dependentTask);
        }
      }
    }

    @Override
    public Collection<Task> dependencies() {
      return task.dependencies();
    }

    public void addDependentTask(ConcurrentTask task) {
      dependentTasks.add(task);
    }

    public List<ConcurrentTask> getDependentTasks() {
      return dependentTasks;
    }

    public boolean solvedDependency() {
      return unsolvedDependenciesCount.decrementAndGet() == 0;
    }
  }
}
