package scheduler;

import java.util.Collection;

public interface Task {
  // выполняет задачу
  void execute();

  // возвращает зависимости для данной задачи
  Collection<Task> dependencies();
}
