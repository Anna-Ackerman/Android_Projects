package ua.cn.stu.randomgallery.app.tasks;

public interface TaskSubject<T> {
    void addListener(TaskListener<T> listener);
    void removeListener(TaskListener<T> listener);
    void cancel();
}