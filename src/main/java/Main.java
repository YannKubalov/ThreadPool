
public class Main {
    public static void main(String[] args) {
        var threadPool = new ThreadPool(5);

        for (int i = 1; i <= 10; i++) {
            var taskId = i;
            threadPool.execute(() -> {
                System.out.println("Задача " + taskId + " начата");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Задача " + taskId + " завершена");
            });
        }

        threadPool.shutdown();

        try {
            threadPool.execute(() -> System.out.println("Эта задача не должна запускаться"));
        } catch (IllegalStateException e) {
            System.out.println("Сообщение об ошибке: " + e.getMessage());
        }

        try {
            threadPool.awaitTermination();
        } catch (InterruptedException e) {
            System.out.println(e.getStackTrace());
        }

        System.out.println("Все задачи выполены");
    }
}