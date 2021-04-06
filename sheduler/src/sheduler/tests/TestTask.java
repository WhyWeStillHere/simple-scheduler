package sheduler.tests;

import sheduler.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class TestTask implements Task {
  protected final Collection<TestTask> dependencies = new ArrayList<>();
  protected boolean isCompleted = false;
  protected boolean invalidOrder = false;

  // Synchronized added for testing purposes
  @Override
  public synchronized void execute() {
    if (isCompleted) {
      invalidOrder = true;
    }
    for (TestTask task : dependencies) {
      if (!task.isCompleted()) {
        invalidOrder = true;
        break;
      }
    }
    isCompleted = true;
  }

  @Override
  public Collection<Task> dependencies() {
    return dependencies
        .stream()
        .map(task -> (Task) task)
        .collect(Collectors.toList());
  }

  public boolean isCompleted() {
    return isCompleted;
  }

  public boolean isValid() {
    return !invalidOrder;
  }

  public void addDependency(Task task) {
    if (!(task instanceof TestTask)) {
      return;
    }
    dependencies.add((TestTask) task);
  }
}
