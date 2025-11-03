package ua.cn.stu.randomgallery.app.storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PhotoStorage {

    private static final String GALLERY_DIR = "gallery_images";
    private static final String PREFS_NAME = "gallery_metadata";
    private static final String KEY_PREFIX_NAME = "photo_name_";
    private static final String KEY_TIMESTAMP = "gallery_timestamp";

    private final File galleryDir;
    private final SharedPreferences preferences;

    public PhotoStorage(Context context) {
        File filesDir = context.getFilesDir();
        this.galleryDir = new File(filesDir, GALLERY_DIR);
        if (!galleryDir.exists()) {
            galleryDir.mkdirs();
        }
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public InputStream read(String identifier) throws IOException {
        File file = new File(galleryDir, identifier);
        if (!file.exists()) {
            throw new IOException("File not found: " + identifier);
        }
        return new FileInputStream(file);
    }

    public OutputStream write(String identifier, String name) throws IOException {
        File file = new File(galleryDir, identifier);
        preferences.edit().putString(KEY_PREFIX_NAME + identifier, name).apply();
        return new FileOutputStream(file);
    }

    public boolean isExists(String identifier) {
        File file = new File(galleryDir, identifier);
        return file.exists();
    }

    public String getName(String identifier) {
        return preferences.getString(KEY_PREFIX_NAME + identifier, null);
    }

    public void delete(String identifier) {
        File file = new File(galleryDir, identifier);
        if (file.exists()) {
            file.delete();
        }
        preferences.edit().remove(KEY_PREFIX_NAME + identifier).apply();
    }

    public List<String> getAll() {
        List<String> ids = new ArrayList<>();
        File[] files = galleryDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    ids.add(file.getName());
                }
            }
        }
        return ids;
    }

    public long getTimestamp() {
        return preferences.getLong(KEY_TIMESTAMP, 0L);
    }

    public void setTimestamp(long timestamp) {
        preferences.edit().putLong(KEY_TIMESTAMP, timestamp).apply();
    }

    public void clear() {
        File[] files = galleryDir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        preferences.edit().clear().apply();
    }
}
