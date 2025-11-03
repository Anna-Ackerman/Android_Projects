package ua.cn.stu.randomgallery.app;

import android.app.Application;

import com.squareup.picasso.Picasso;

import ua.cn.stu.randomgallery.app.gallery.GalleryClient;
import ua.cn.stu.randomgallery.app.storage.PhotoStorage;

public class App extends Application {

    private GalleryClient galleryClient;
    private Picasso picasso;

    @Override
    public void onCreate() {
        super.onCreate();

        PhotoStorage photoStorage = new PhotoStorage(this);
        galleryClient = new GalleryClient(photoStorage);

        GalleryRequestHandler requestHandler = new GalleryRequestHandler();
        galleryClient.addListener(requestHandler);

        picasso = new Picasso.Builder(this)
                .addRequestHandler(requestHandler)
                .build();
    }

    public GalleryClient getGalleryClient() {
        return galleryClient;
    }

    public Picasso getPicasso() {
        return picasso;
    }
}