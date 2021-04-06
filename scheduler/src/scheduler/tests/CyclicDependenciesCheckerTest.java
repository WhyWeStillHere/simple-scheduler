package scheduler.tests;
import org.junit.jupiter.api.Test;
import scheduler.CyclicDependenciesChecker;
import scheduler.EmptyTask;
import scheduler.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CyclicDependenciesCheckerTest {
  @Test
  void findCyclicDependencies_noCycle_empty() {
    CyclicDependenciesChecker checker = new CyclicDependenciesChecker();

    assertFalse(checker.findCyclicDependencies(new ArrayList<>()));
  }

  @Test
  void findCyclicDependencies_noCycle_1() {
    CyclicDependenciesChecker checker = new CyclicDependenciesChecker();
    EmptyTask task = new EmptyTask();

    assertFalse(checker.findCyclicDependencies(Collections.singletonList(task)));
  }

  @Test
  void findCyclicDependencies_noCycle_2() {
    CyclicDependenciesChecker checker = new CyclicDependenciesChecker();
    List<Task> tasks = new ArrayList<>();
    for (int i = 0; i < 100; ++i) {
      tasks.add(new EmptyTask());
    }

    assertFalse(checker.findCyclicDependencies(tasks));
  }

  @Test
  void findCyclicDependencies_noCycle_3() {
    CyclicDependenciesChecker checker = new CyclicDependenciesChecker();
    List<EmptyTask> tasks = new ArrayList<>();
    for (int i = 0; i < 5; ++i) {
      tasks.add(new EmptyTask());
    }
    tasks.get(0).addDependency(tasks.get(2));
    tasks.get(1).addDependency(tasks.get(2));
    tasks.get(1).addDependency(tasks.get(3));
    tasks.get(3).addDependency(tasks.get(2));
    tasks.get(2).addDependency(tasks.get(4));

    assertFalse(
        checker.findCyclicDependencies(
            tasks
                .stream()
                .map(task -> (Task) task)
                .collect(Collectors.toList())));
  }

  @Test
  void findCyclicDependencies_noCycle_bamboo() {
    CyclicDependenciesChecker checker = new CyclicDependenciesChecker();
    List<EmptyTask> tasks = new ArrayList<>();
    for (int i = 0; i < 2_000_000; ++i) {
      tasks.add(new EmptyTask());
    }

    for (int i = 0; i < tasks.size(); ++i) {
      if (i != tasks.size() - 1) {
        tasks.get(i).addDependency(tasks.get(i + 1));
      }
    }

    assertFalse(
        checker.findCyclicDependencies(
            tasks
                .stream()
                .map(task -> (Task) task)
                .collect(Collectors.toList())));
  }

  @Test
  void findCyclicDependencies_bigCycle() {
    CyclicDependenciesChecker checker = new CyclicDependenciesChecker();
    List<EmptyTask> tasks = new ArrayList<>();
    for (int i = 0; i < 100; ++i) {
      tasks.add(new EmptyTask());
    }

    for (int i = 0; i < 100; ++i) {
      tasks.get(i).addDependency(tasks.get((i + 1) % 100));
    }

    assertTrue(
        checker.findCyclicDependencies(
            tasks
                .stream()
                .map(task -> (Task) task)
                .collect(Collectors.toList())));
  }

  @Test
  void findCyclicDependencies_selfDependency() {
    CyclicDependenciesChecker checker = new CyclicDependenciesChecker();
    EmptyTask task = new EmptyTask();
    task.addDependency(task);

    assertTrue(checker.findCyclicDependencies(Collections.singletonList(task)));
  }

  @Test
  void findCyclicDependencies_twoTaskDependency() {
    CyclicDependenciesChecker checker = new CyclicDependenciesChecker();
    EmptyTask task_1 = new EmptyTask();
    EmptyTask task_2 = new EmptyTask();
    task_1.addDependency(task_2);
    task_2.addDependency(task_1);

    assertTrue(checker.findCyclicDependencies(Arrays.asList(task_1, task_2)));
  }

  @Test
  void findCyclicDependencies_multipleComponents() {
    CyclicDependenciesChecker checker = new CyclicDependenciesChecker();
    List<EmptyTask> tasks = new ArrayList<>();
    for (int i = 0; i < 200; ++i) {
      tasks.add(new EmptyTask());
    }
    tasks.get(0).addDependency(tasks.get(2));
    tasks.get(1).addDependency(tasks.get(2));
    tasks.get(1).addDependency(tasks.get(3));
    tasks.get(3).addDependency(tasks.get(2));
    tasks.get(2).addDependency(tasks.get(4));

    for (int i = 5; i < 100; ++i) {
      if (i != 99) {
        tasks.get(i).addDependency(tasks.get(i + 1));
      }
    }

    for (int i = 100; i < 200; ++i) {
      if (i != 199) {
        tasks.get(i).addDependency(tasks.get(i + 1));
      } else {
        tasks.get(i).addDependency(tasks.get(100));
      }
    }

    assertTrue(
        checker.findCyclicDependencies(
            tasks
                .stream()
                .map(task -> (Task) task)
                .collect(Collectors.toList())));
  }
}
