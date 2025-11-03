package ua.cn.stu.randomgallery.app.tasks;

public interface TaskListener<T> {
    void onResults(Result<T> results);
}
