package ua.cn.stu.randomgallery.app.tasks;

import java.util.concurrent.Callable;

import ua.cn.stu.randomgallery.app.gallery.GalleryClient;
import ua.cn.stu.randomgallery.app.gallery.ProgressCallback;

public class GalleryTasks {

    private final GalleryClient client;

    public GalleryTasks(GalleryClient client) {
        this.client = client;
    }

    public Callable<Boolean> createCheckUpdatesTask() {
        return new CheckUpdatesTask(client);
    }

    public Callable<TaskResultsFragment.SyncResult> createSyncTask(ProgressCallback progressCallback) {
        return new SyncTask(client, progressCallback);
    }
}
