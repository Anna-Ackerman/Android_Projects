package ua.cn.stu.randomgallery.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import ua.cn.stu.randomgallery.app.App;
import ua.cn.stu.randomgallery.app.BuildConfig;
import ua.cn.stu.randomgallery.app.R;
import ua.cn.stu.randomgallery.app.gallery.GalleryClient;

public class ActionsHandlerService extends Service {

    public static final String TAG = ActionsHandlerService.class.getSimpleName();
    public static final String ACTION_CHECK_FOR_UPDATES =
            BuildConfig.APPLICATION_ID + ".CHECK_FOR_UPDATES";
    public static final String ACTION_CANCEL =
            BuildConfig.APPLICATION_ID + ".CANCEL";
    public static final String EXTRA_FROM_NOTIFICATION = "FROM_NOTIFICATION";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        String action = intent.getAction();
        switch (action) {
            case ACTION_CHECK_FOR_UPDATES:
                boolean fromNotification = intent.getBooleanExtra(EXTRA_FROM_NOTIFICATION, false);
                checkForUpdates(fromNotification);
                break;
            case ACTION_CANCEL:
                Toast.makeText(this, R.string.cancelled, Toast.LENGTH_SHORT).show();
                stopSelf();
                break;
        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void checkForUpdates(boolean fromNotification) {
        App app = (App) getApplicationContext();
        GalleryClient client = app.getGalleryClient();

        new Thread(() -> {
            try {
                boolean hasUpdates = client.hasUpdates();
                app.getSyncState().setHasUpdates(hasUpdates);
                app.getSyncState().notifyListeners();

                if (fromNotification && hasUpdates) {
                    showToast();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error", e);
            }
            stopSelf();
        }).start();
    }

    private void showToast() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(this, R.string.update_available, Toast.LENGTH_SHORT).show();
        });
    }
}