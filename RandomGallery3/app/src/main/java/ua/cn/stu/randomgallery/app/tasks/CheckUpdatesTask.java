package ua.cn.stu.randomgallery.app.tasks;

import java.util.concurrent.Callable;

import ua.cn.stu.randomgallery.app.gallery.GalleryClient;

public class CheckUpdatesTask implements Callable<Boolean> {

    private final GalleryClient client;

    public CheckUpdatesTask(GalleryClient client) {
        this.client = client;
    }

    @Override
    public Boolean call() throws Exception {
        return client.hasUpdates();
    }
}