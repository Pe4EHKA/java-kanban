import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

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

        SubTask subTask1_1 = new SubTask("JavaCore", "Изучить основы Java", epic1.getId());
        subTask1_1.setStatus(Status.IN_PROGRESS);
        SubTask subTask1_2 = new SubTask("Spring", "Изучить фреймворк Spring", epic1.getId());
        taskManager.createSubTask(subTask1_1);
        taskManager.createSubTask(subTask1_2);

        Epic epic2 = new Epic("Сдать курсовой проект", "Подготовить все материалы");
        taskManager.createEpic(epic2);

        SubTask subTask2_1 = new SubTask("Презентация",
                "Сделать презентацию по проекту", epic2.getId());
        subTask2_1.setStatus(Status.DONE);
        taskManager.createSubTask(subTask2_1);

        System.out.println("--------------------------");
        System.out.println("Задачи в историю добавляются в конец списка");
        taskManager.getTask(task1.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getTask(task2.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getEpic(epic1.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getSubTask(subTask1_1.getId());
        taskManager.getSubTask(subTask1_2.getId());
        System.out.println(taskManager.getHistory());
        System.out.println("--------------------------");
        System.out.println("Задачи в истории не дублируются и если одна из них была уже в истории," +
                        " то перемещается в конец");
        taskManager.getTask(task1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println("--------------------------");
        System.out.println("Вместе с эпиками из истории удаляются их подзадачи");
        taskManager.deleteAllEpics();
        System.out.println(taskManager.getHistory());
        System.out.println("--------------------------");
        System.out.println("Все эпики:");
        System.out.println(taskManager.getEpics());
        System.out.println("Все задачи:");
        System.out.println(taskManager.getTasks());
        System.out.println("Все подзадачи:");
        System.out.println(taskManager.getSubTasks());
    }
}
