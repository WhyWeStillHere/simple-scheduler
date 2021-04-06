package sheduler.tests;

public class SleepyTask extends TestTask {
  private final int sleepTime;

  public SleepyTask(int sleepTime) {
    super();
    this.sleepTime = sleepTime;
  }


  @Override
  public synchronized void execute() {
    try {
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    for (TestTask task : dependencies) {
      if (!task.isCompleted) {
        invalidOrder = true;
        break;
      }
    }
    isCompleted = true;
  }
}
