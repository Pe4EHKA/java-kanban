import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.FileBackedTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefaults();

        Task task1 = new Task("Закончить 4 спринт",
                "Выполнить правильно финальный проект");
        taskManager.createTask(task1);

        Task task2 = new Task("Мультфильм", "Посмотреть Спанч-Боба");
        task2.setStatus(Status.DONE);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Стать Java-разработчиком", "Учиться и практиковаться");
        taskManager.createEpic(epic1);

        SubTask subTask11 = new SubTask("JavaCore", "Изучить основы Java", epic1.getId());
        subTask11.setStatus(Status.IN_PROGRESS);
        SubTask subTask12 = new SubTask("Spring", "Изучить фреймворк Spring", epic1.getId());
        taskManager.createSubTask(subTask11);
        taskManager.createSubTask(subTask12);

        Epic epic2 = new Epic("Сдать курсовой проект", "Подготовить все материалы");
        taskManager.createEpic(epic2);

        SubTask subTask21 = new SubTask("Презентация",
                "Сделать презентацию по проекту", epic2.getId());
        subTask21.setStatus(Status.DONE);
        taskManager.createSubTask(subTask21);
        System.out.println("--------taskManager-----");
        System.out.println("Все задачи:");
        System.out.println(taskManager.getTasks());
        System.out.println("Все эпики:");
        System.out.println(taskManager.getEpics());
        System.out.println("Все подзадачи:");
        System.out.println(taskManager.getSubTasks());

        TaskManager taskManagerFileBacked = FileBackedTaskManager.loadFromFileStatic(new File("tasks.csv"));

        System.out.println("--------taskManagerFileBacked------");
        System.out.println("Все задачи:");
        System.out.println(taskManagerFileBacked.getTasks());
        System.out.println("Все эпики:");
        System.out.println(taskManagerFileBacked.getEpics());
        System.out.println("Все подзадачи:");
        System.out.println(taskManagerFileBacked.getSubTasks());
    }
}
