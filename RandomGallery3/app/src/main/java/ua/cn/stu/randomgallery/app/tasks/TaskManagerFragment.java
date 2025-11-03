package ua.cn.stu.randomgallery.app.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskManagerFragment extends Fragment {

    public static final String TAG = TaskManagerFragment.class.getSimpleName();

    private ExecutorService executorService;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow();
    }

    public <T> TaskSubject<T> submitTask(Callable<T> callable) {
        TaskSubjectImpl<T> taskSubject = new TaskSubjectImpl<>();
        taskSubject.future = executorService.submit(() -> {
            try {
                T result = callable.call();
                taskSubject.setResult(Result.success(result));
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    taskSubject.setResult(Result.error(new CancelledException()));
                } else {
                    taskSubject.setResult(Result.error(e));
                }
            }
        });
        return taskSubject;
    }

    class TaskSubjectImpl<T> implements TaskSubject<T> {

        private Result<T> result = Result.inProgress();
        private volatile Set<TaskListener<T>> listeners = new LinkedHashSet<>();
        private Future<?> future;

        @Override
        public synchronized void addListener(TaskListener<T> listener) {
            this.listeners.add(listener);
            listener.onResults(result);
        }

        @Override
        public synchronized void removeListener(TaskListener<T> listener) {
            this.listeners.remove(listener);
        }

        @Override
        public void cancel() {
            if (!future.isCancelled()) {
                setResult(Result.error(new CancelledException()));
                future.cancel(true);
            }
        }

        void setResult(Result<T> result) {
            if (isMainThread()) {
                doSetResult(result);
            } else {
                handler.post(() -> doSetResult(result));
            }
        }

        private void doSetResult(Result<T> result) {
            if (this.result.getStatus() != Status.IN_PROGRESS) {
                return;
            }
            this.result = result;
            List<TaskListener<T>> listeners = new ArrayList<>(this.listeners);
            for (TaskListener<T> listener : listeners) {
                listener.onResults(result);
            }
        }

        private boolean isMainThread() {
            return Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId();
        }
    }
}
