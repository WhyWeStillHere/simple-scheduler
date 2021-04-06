package scheduler.tests;

public class ErrorTask extends TestTask {
  @Override
  public synchronized void execute() {
    throw new RuntimeException("Unexpected error!!!");
  }
}
