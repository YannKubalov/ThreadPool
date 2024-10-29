import java.util.LinkedList;
import java.util.Queue;

public class ThreadPool {
    private final int capacity;
    private final Queue<Runnable> taskQueue;
    private final WorkerThread[] workers;
    private boolean isShutdown;

    public ThreadPool(int capacity) {
        this.capacity = capacity;
        this.taskQueue = new LinkedList<>();
        this.workers = new WorkerThread[capacity];
        this.isShutdown = false;

        for (int i = 0; i < capacity; i++) {
            workers[i] = new WorkerThread();
            workers[i].start();
        }
    }

    public void execute(Runnable task) {
        synchronized (taskQueue) {
            if (isShutdown) {
                throw new IllegalStateException("Пул потоков закрыт, новые задачи не добавляются");
            }
            taskQueue.offer(task);
            taskQueue.notify();
        }
    }

    public void shutdown() {
        synchronized (taskQueue){
            isShutdown = true;
            taskQueue.notify();
        }
    }

    public synchronized void awaitTermination() throws InterruptedException {
        for (WorkerThread worker : workers) {
            worker.join();
        }
    }

    private class WorkerThread extends Thread {
        public void run() {
            while (true) {
                Runnable task;
                synchronized (taskQueue) {
                    while (taskQueue.isEmpty() && !isShutdown) {
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    if (isShutdown && taskQueue.isEmpty()) {
                        break;
                    }
                    task = taskQueue.poll();
                }
                if (task != null) {
                    task.run();
                }
            }
        }
    }
}
