package ua.cn.stu.randomgallery.app.screens;

import android.os.Handler;
import android.os.Looper;

public class MainThreadHandler {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void run(Runnable runnable) {
        if (Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId()) {
            runnable.run();
        } else {
            HANDLER.post(() -> {
                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
