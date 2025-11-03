package ua.cn.stu.randomgallery.app.tasks;

import java.util.concurrent.Callable;

import ua.cn.stu.randomgallery.app.gallery.GalleryClient;
import ua.cn.stu.randomgallery.app.gallery.ProgressCallback;

public class SyncTask implements Callable<TaskResultsFragment.SyncResult> {

    private final GalleryClient client;
    private final ProgressCallback progressCallback;

    public SyncTask(GalleryClient client, ProgressCallback progressCallback) {
        this.client = client;
        this.progressCallback = progressCallback;
    }

    @Override
    public TaskResultsFragment.SyncResult call() throws Exception {
        final int[] lastProgress = {0};

        boolean success = client.syncGallery(percentage -> {
            lastProgress[0] = percentage;
            if (progressCallback != null) {
                progressCallback.onProgress(percentage);
            }
        });

        return new TaskResultsFragment.SyncResult(success, lastProgress[0]);
    }
}
