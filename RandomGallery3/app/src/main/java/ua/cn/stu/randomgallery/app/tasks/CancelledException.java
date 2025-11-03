package ua.cn.stu.randomgallery.app.tasks;

public class CancelledException extends RuntimeException {
    public CancelledException() {
        super("Task has been cancelled");
    }
}
