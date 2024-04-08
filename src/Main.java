import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Закончить 4 спринт",
                "Выполнить правильно финальный проект", Status.NEW);
        taskManager.createTask(task1);

        Task task2 = new Task("Мультфильм", "Посмотреть Спанч-Боба", Status.DONE);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Стать Java-разработчиком", "Учиться и практиковаться");
        taskManager.createEpic(epic1);

        SubTask subTask1_1 = new SubTask("JavaCore", "Изучить основы Java", Status.IN_PROGRESS, epic1);
        SubTask subTask1_2 = new SubTask("Spring", "Изучить фреймворк Spring", Status.NEW, epic1);
        taskManager.createSubTask(subTask1_1);
        taskManager.createSubTask(subTask1_2);

        Epic epic2 = new Epic("Сдать курсовой проект", "Подготовить все материалы");
        taskManager.createEpic(epic2);

        SubTask subTask2_1 = new SubTask("Презентация",
                "Сделать презентацию по проекту", Status.DONE, epic2);
        taskManager.createSubTask(subTask2_1);

        System.out.println("Все эпики:");
        System.out.println(taskManager.getEpics());
        System.out.println("Все задачи:");
        System.out.println(taskManager.getTasks());
        System.out.println("Все подзадачи:");
        System.out.println(taskManager.getSubTasks());

        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        taskManager.deleteByIdTask(task2.getId());

        epic1.setName("Biba and Boba");
        epic1.setDescription("Someday it will be all right");
        taskManager.deleteByIdSubTask(subTask1_2.getId());
        taskManager.updateEpic(epic1);
        subTask1_1.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1_1);

        taskManager.deleteByIdEpic(epic2.getId());

        System.out.println("--------------------------");
        System.out.println("Все эпики:");
        System.out.println(taskManager.getEpics());
        System.out.println("Все задачи:");
        System.out.println(taskManager.getTasks());
        System.out.println("Все подзадачи:");
        System.out.println(taskManager.getSubTasks());

    }


}
