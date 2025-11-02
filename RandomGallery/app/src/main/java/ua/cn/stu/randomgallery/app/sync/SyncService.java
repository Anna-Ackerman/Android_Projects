package ua.cn.stu.randomgallery.app.sync;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ua.cn.stu.randomgallery.app.App;
import ua.cn.stu.randomgallery.app.R;
import ua.cn.stu.randomgallery.app.gallery.GalleryClient;

public class SyncService extends JobService {

    private static final String TAG = SyncService.class.getSimpleName();
    private static final int JOB_ID = 1;
    private static final int NOTIFICATION_ID = 100;
    private static final String CHANNEL_ID = "sync_channel";

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future<?> future;
    private SyncState syncState;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        App app = (App) getApplicationContext();
        syncState = app.getSyncState();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, createNotification(0));
        }

        future = executorService.submit(() -> {
            GalleryClient client = app.getGalleryClient();
            boolean success = false;
            try {
                success = client.syncGallery(percentage -> {
                    syncState.setProgressPercentage(percentage);
                    syncState.notifyListeners();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationManager manager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(NOTIFICATION_ID, createNotification(percentage));
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error", e);
            }

            if (success) {
                finish(true);
            } else {
                finish(false);
            }
            jobFinished(params, !success);
        });

        syncState.setInProgress(true);
        syncState.notifyListeners();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        future.cancel(true);
        finish(false);
        return false;
    }

    private void finish(boolean success) {
        syncState.setInProgress(false);
        syncState.setScheduled(false);
        syncState.setHasUpdates(!success);
        syncState.notifyListeners();

        if (success) {
            syncState.onSyncFinished();
        } else {
            syncState.onSyncFailed();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Gallery Sync",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private android.app.Notification createNotification(int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.updating_gallery))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        if (progress > 0) {
            builder.setContentText(getString(R.string.percentage, progress))
                    .setProgress(100, progress, false);
        } else {
            builder.setProgress(100, 0, true);
        }

        return builder.build();
    }

    public static void scheduleUpdate(Context context) {
        App app = (App) context.getApplicationContext();
        JobScheduler jobScheduler =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        JobInfo jobInfo = new JobInfo.Builder(
                JOB_ID,
                new ComponentName(context, SyncService.class)
        )
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(0)
                .build();

        jobScheduler.schedule(jobInfo);
        app.getSyncState().setScheduled(true);
        app.getSyncState().notifyListeners();
    }

    static boolean isScheduled(Context context) {
        JobScheduler jobScheduler =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        List<JobInfo> jobs = jobScheduler.getAllPendingJobs();
        for (JobInfo job : jobs) {
            if (job.getId() == JOB_ID) return true;
        }
        return false;
    }
}