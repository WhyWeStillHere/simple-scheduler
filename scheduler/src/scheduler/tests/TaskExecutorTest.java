package scheduler.tests;

import org.junit.jupiter.api.Test;
import scheduler.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TaskExecutorTest {
  void checkCorrectness(Collection<TestTask> tasks) {
    for (TestTask task : tasks) {
      assertTrue(task.isCompleted());
      assertTrue(task.isValid());
    }
  }

  @Test
  void execute_cyclicDependency() {
    TaskExecutor executor = new TaskExecutor();
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

    assertThrows(
        IllegalArgumentException.class,
        () -> executor.execute(
            tasks
                .stream()
                .map(task -> (Task) task)
                .collect(Collectors.toList())));
  }

  @Test
  void execute_noTasks() {
    TaskExecutor executor = new TaskExecutor();
    List<Task> tasks = new ArrayList<>();

    executor.execute(tasks);
    executor.execute(null);
  }

  @Test
  void execute_exception() {
    TaskExecutor executor = new TaskExecutor();
    List<TestTask> tasks = new ArrayList<>();
    tasks.add(new TestTask());
    tasks.add(new ErrorTask());
    tasks.get(0).addDependency(tasks.get(1));

    executor.execute(tasks
        .stream()
        .map(task -> (Task) task)
        .collect(Collectors.toList()));
  }

  @Test
  void execute_complexConstruction() {
    TaskExecutor executor = new TaskExecutor();
    List<TestTask> tasks = new ArrayList<>();
    for (int i = 0; i < 9; ++i) {
      tasks.add(new TestTask());
    }
    tasks.get(0).addDependency(tasks.get(4));
    tasks.get(1).addDependency(tasks.get(4));
    tasks.get(2).addDependency(tasks.get(4));
    tasks.get(3).addDependency(tasks.get(4));
    tasks.get(4).addDependency(tasks.get(5));
    tasks.get(4).addDependency(tasks.get(6));
    tasks.get(4).addDependency(tasks.get(7));
    tasks.get(4).addDependency(tasks.get(8));

    executor.execute(tasks
        .stream()
        .map(task -> (Task) task)
        .collect(Collectors.toList()));

    checkCorrectness(tasks);
  }

  @Test
  void execute_bamboo() {
    TaskExecutor executor = new TaskExecutor();
    List<TestTask> tasks = new ArrayList<>();
    for (int i = 0; i < 100_000; ++i) {
      tasks.add(new TestTask());
    }

    for (int i = 0; i < tasks.size(); ++i) {
      if (i != tasks.size() - 1) {
        tasks.get(i).addDependency(tasks.get(i + 1));
      }
    }

    executor.execute(tasks
        .stream()
        .map(task -> (Task) task)
        .collect(Collectors.toList()));

    checkCorrectness(tasks);
  }

  @Test
  void execute_noDependency() {
    TaskExecutor executor = new TaskExecutor();
    List<TestTask> tasks = new ArrayList<>();
    for (int i = 0; i < 1_000_000; ++i) {
      tasks.add(new TestTask());
    }

    executor.execute(tasks
        .stream()
        .map(task -> (Task) task)
        .collect(Collectors.toList()));

    checkCorrectness(tasks);
  }

  @Test
  void execute_layeredNetwork() {
    int layerSize = 60;
    int layersNum = 200;
    TaskExecutor executor = new TaskExecutor();
    List<TestTask> tasks = new ArrayList<>();

    for (int i = 0; i < layerSize * layersNum; ++i) {
      TestTask currentTask = new TestTask();
      tasks.add(currentTask);

      int layerIndex = i / layerSize;
      if (layerIndex > 0) {
        --layerIndex;
        for (int j = 0; j < layerSize; ++j) {
          currentTask.addDependency(tasks.get(layerIndex * layerSize + j));
        }
      }
    }

    executor.execute(tasks
        .stream()
        .map(task -> (Task) task)
        .collect(Collectors.toList()));

    checkCorrectness(tasks);
  }

  @Test
  void execute_concurrency() {
    int sleepTime = 1000;
    TaskExecutor executor = new TaskExecutor();
    List<TestTask> tasks = new ArrayList<>();
    for (int i = 0; i < 6; ++i) {
      tasks.add(new SleepyTask(sleepTime));
    }

    long startTime = System.currentTimeMillis();
    executor.execute(tasks
        .stream()
        .map(task -> (Task) task)
        .collect(Collectors.toList()));
    long endTime = System.currentTimeMillis();

    checkCorrectness(tasks);
    assertTrue((endTime - startTime) < sleepTime * 2);
  }

  @Test
  void execute_stressTest() {
    Random random = new Random();
    TaskExecutor executor = new TaskExecutor();
    List<TestTask> tasks = new ArrayList<>();
    for (int i = 0; i < 2_000_000; ++i) {
      tasks.add(new TestTask());
    }

    for (int i = 0; i < tasks.size(); ++i) {
      if (i != tasks.size() - 1) {
        int offset = random.nextInt(tasks.size() - i  - 1) + 1;
        tasks.get(i).addDependency(tasks.get(i + offset));
      }
    }

    executor.execute(tasks
        .stream()
        .map(task -> (Task) task)
        .collect(Collectors.toList()));

    checkCorrectness(tasks);
  }
}
