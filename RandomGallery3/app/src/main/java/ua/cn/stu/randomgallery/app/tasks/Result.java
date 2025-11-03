package ua.cn.stu.randomgallery.app.tasks;

public class Result<T> {
    private T data;
    private Throwable error;
    private Status status;

    private Result(T data, Throwable error, Status status) {
        this.data = data;
        this.error = error;
        this.status = status;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data, null, Status.SUCCESS);
    }

    public static <T> Result<T> error(Throwable error) {
        return new Result<>(null, error, Status.ERROR);
    }

    public static <T> Result<T> inProgress() {
        return new Result<>(null, null, Status.IN_PROGRESS);
    }

    public T getData() {
        return data;
    }

    public Throwable getError() {
        return error;
    }

    public Status getStatus() {
        return status;
    }
}
