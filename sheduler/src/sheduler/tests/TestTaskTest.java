package sheduler.tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTaskTest {
  @Test
  void execute_oneTask() {
    TestTask task = new TestTask();
    assertFalse(task.isCompleted());
    assertTrue(task.isValid());
    task.execute();
    assertTrue(task.isCompleted());
    assertTrue(task.isValid());
  }

  @Test
  void execute_oneTask_multipleTimes() {
    TestTask task = new TestTask();
    assertTrue(task.isValid());
    task.execute();
    task.execute();
    assertFalse(task.isValid());
  }

  @Test
  void execute_dependenciesNoError_1() {
    TestTask task_1 = new TestTask();
    TestTask task_2 = new TestTask();
    task_1.addDependency(task_2);

    assertFalse(task_2.isCompleted());
    assertTrue(task_2.isValid());
    task_2.execute();
    assertTrue(task_2.isCompleted());
    assertTrue(task_2.isValid());

    assertFalse(task_1.isCompleted());
    assertTrue(task_1.isValid());
    task_1.execute();
    assertTrue(task_1.isCompleted());
    assertTrue(task_1.isValid());
  }

  @Test
  void execute_dependenciesNoError_2() {
    TestTask task_1 = new TestTask();
    TestTask task_2 = new TestTask();
    TestTask task_3 = new TestTask();
    task_1.addDependency(task_2);
    task_1.addDependency(task_3);

    assertFalse(task_2.isCompleted());
    assertTrue(task_2.isValid());
    task_2.execute();
    assertTrue(task_2.isCompleted());
    assertTrue(task_2.isValid());

    assertFalse(task_3.isCompleted());
    assertTrue(task_3.isValid());
    task_3.execute();
    assertTrue(task_3.isCompleted());
    assertTrue(task_3.isValid());

    assertFalse(task_1.isCompleted());
    assertTrue(task_1.isValid());
    task_1.execute();
    assertTrue(task_1.isCompleted());
    assertTrue(task_1.isValid());
  }

  @Test
  void execute_dependenciesError_1() {
    TestTask task_1 = new TestTask();
    TestTask task_2 = new TestTask();
    task_1.addDependency(task_2);

    assertTrue(task_1.isValid());
    task_1.execute();
    assertFalse(task_1.isValid());
  }

  @Test
  void execute_dependenciesError_2() {
    TestTask task_1 = new TestTask();
    TestTask task_2 = new TestTask();
    TestTask task_3 = new TestTask();
    task_1.addDependency(task_2);
    task_2.addDependency(task_3);

    assertTrue(task_2.isValid());
    task_2.execute();
    assertFalse(task_2.isValid());
  }
}
