package sheduler;

import sheduler.tests.TestTask;

import java.util.ArrayList;
import java.util.Collection;

public class EmptyTask implements Task {
  private final Collection<Task> dependencies = new ArrayList<>();

  @Override
  public void execute() {
  }

  @Override
  public Collection<Task> dependencies() {
    return dependencies;
  }

  public void addDependency(Task task) {
    dependencies.add(task);
  }
}
