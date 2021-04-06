package scheduler.tests;

import org.junit.jupiter.api.Test;
import scheduler.ExecutionQueue;
import scheduler.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutionQueueTest {
  void checkCorrectness(Collection<TestTask> tasks) {
    for (TestTask task : tasks) {
      assertTrue(task.isCompleted());
      assertTrue(task.isValid());
    }
  }

  void executeMultipleTaskConcurrently(ExecutionQueue executionQueue, int threadNum) throws InterruptedException {
    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < threadNum; ++i) {
      threads.add(new Thread(()-> executionQueue.availableTask().execute()));
    }
    for (int i = 0; i < threadNum; ++i) {
      threads.get(i).start();
    }
    for (int i = 0; i < threadNum; ++i) {
      threads.get(i).join();
    }
  }

  @Test
  void availableTask_oneTask() {
    ExecutionQueue executionQueue = new ExecutionQueue();
    TestTask task = new TestTask();
    executionQueue.Build(Collections.singleton(task));

    executionQueue.availableTask().execute();
    assertNull(executionQueue.availableTask());

    checkCorrectness(Collections.singleton(task));
  }

  @Test
  void availableTask_bamboo() {
    ExecutionQueue executionQueue = new ExecutionQueue();
    List<TestTask> tasks = new ArrayList<>();
    for (int i = 0; i < 100_000; ++i) {
      tasks.add(new TestTask());
    }

    for (int i = 0; i < tasks.size(); ++i) {
      if (i != tasks.size() - 1) {
        tasks.get(i).addDependency(tasks.get(i + 1));
      }
    }

    executionQueue.Build(tasks
        .stream()
        .map(task -> (Task) task)
        .collect(Collectors.toList()));

    for (int i = 0; i < tasks.size(); ++i) {
      executionQueue.availableTask().execute();
    }
    assertNull(executionQueue.availableTask());

    checkCorrectness(tasks);
  }

  @Test
  void availableTask_multipleComponents() {
    ExecutionQueue executionQueue = new ExecutionQueue();
    List<TestTask> tasks = new ArrayList<>();
    for (int i = 0; i < 10; ++i) {
      tasks.add(new TestTask());
    }
    tasks.get(0).addDependency(tasks.get(2));
    tasks.get(1).addDependency(tasks.get(2));
    tasks.get(1).addDependency(tasks.get(3));
    tasks.get(3).addDependency(tasks.get(2));
    tasks.get(2).addDependency(tasks.get(4));

    tasks.get(5).addDependency(tasks.get(9));
    tasks.get(6).addDependency(tasks.get(9));
    tasks.get(7).addDependency(tasks.get(9));
    tasks.get(8).addDependency(tasks.get(9));

    executionQueue.Build(tasks
        .stream()
        .map(task -> (Task) task)
        .collect(Collectors.toList()));

    for (int i = 0; i < tasks.size(); ++i) {
      executionQueue.availableTask().execute();
    }
    assertNull(executionQueue.availableTask());

    checkCorrectness(tasks);
  }

  @Test
  void availableTask_nonBlocking() throws InterruptedException {
    int sleepTime = 1000;
    ExecutionQueue executionQueue = new ExecutionQueue();
    List<TestTask> tasks = new ArrayList<>();
    for (int i = 0; i < 6; ++i) {
      tasks.add(new SleepyTask(sleepTime));
    }

    executionQueue.Build(tasks
        .stream()
        .map(task -> (Task) task)
        .collect(Collectors.toList()));

    long startTime = System.currentTimeMillis();
    executeMultipleTaskConcurrently(executionQueue, 6);
    long endTime = System.currentTimeMillis();

    assertNull(executionQueue.availableTask());
    checkCorrectness(tasks);
    assertTrue((endTime - startTime) < sleepTime * 2);
  }

  @Test
  void availableTask_concurrent() throws InterruptedException {
    ExecutionQueue executionQueue = new ExecutionQueue();
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

    executionQueue.Build(tasks
        .stream()
        .map(task -> (Task) task)
        .collect(Collectors.toList()));

    executeMultipleTaskConcurrently(executionQueue, 4);
    executionQueue.availableTask().execute();
    executeMultipleTaskConcurrently(executionQueue, 4);

    assertNull(executionQueue.availableTask());
    checkCorrectness(tasks);
  }
}
