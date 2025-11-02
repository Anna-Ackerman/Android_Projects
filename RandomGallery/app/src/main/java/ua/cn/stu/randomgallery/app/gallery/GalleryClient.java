package ua.cn.stu.randomgallery.app.gallery;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ua.cn.stu.randomgallery.app.storage.PhotoStorage;

public class GalleryClient {

    private static final String TAG = "GalleryClient";
    private static final int PHOTO_COUNT = 20;

    private final PhotoStorage storage;
    private final List<GalleryListener> listeners = new CopyOnWriteArrayList<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public GalleryClient(PhotoStorage storage) {
        this.storage = storage;
        loadPhotosFromStorage();
    }

    public void addListener(GalleryListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GalleryListener listener) {
        listeners.remove(listener);
    }

    public boolean hasUpdates() {
        return storage.getAll().isEmpty();
    }

    public boolean syncGallery(ProgressCallback callback) {
        try {
            List<String> existingIds = storage.getAll();

            if (!existingIds.isEmpty()) {
                callback.onProgress(100);
                notifyListeners();
                return true;
            }

            for (int i = 0; i < PHOTO_COUNT; i++) {
                String photoId = "photo_" + i;
                String photoName = "Random Photo " + (i + 1);
                int width = 400;
                int height = 300;

                String imageUrl = String.format(
                        "https://picsum.photos/%d/%d?random=%d",
                        width, height, System.currentTimeMillis() + i
                );

                Log.d(TAG, "Downloading: " + imageUrl);

                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setInstanceFollowRedirects(true);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();

                    java.io.OutputStream outputStream = storage.write(photoId, photoName);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    inputStream.close();

                    int progress = ((i + 1) * 100) / PHOTO_COUNT;
                    callback.onProgress(progress);

                    Log.d(TAG, "Downloaded: " + photoName + " (" + progress + "%)");
                } else {
                    Log.e(TAG, "Failed to download, response code: " + responseCode);
                }

                connection.disconnect();
            }

            storage.setTimestamp(System.currentTimeMillis());
            notifyListeners();

            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error syncing gallery", e);
            return false;
        }
    }

    private void loadPhotosFromStorage() {
        new Thread(() -> {
            List<LocalPhoto> photos = getPhotosFromStorage();
            mainHandler.post(() -> {
                for (GalleryListener listener : listeners) {
                    listener.onGotGalleryPhotos(photos);
                }
            });
        }).start();
    }

    private void notifyListeners() {
        List<LocalPhoto> photos = getPhotosFromStorage();
        mainHandler.post(() -> {
            for (GalleryListener listener : listeners) {
                listener.onGotGalleryPhotos(photos);
            }
        });
    }

    private List<LocalPhoto> getPhotosFromStorage() {
        List<LocalPhoto> photos = new ArrayList<>();
        List<String> ids = storage.getAll();

        for (String id : ids) {
            String name = storage.getName(id);
            if (name != null) {
                photos.add(new LocalPhoto(id, name, storage::read));
            }
        }

        return photos;
    }
}
