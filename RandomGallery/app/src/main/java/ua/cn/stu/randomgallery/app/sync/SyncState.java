package ua.cn.stu.randomgallery.app.sync;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;

import java.util.HashSet;
import java.util.Set;

public class SyncState {

    private boolean hasUpdates;
    private boolean scheduled;
    private boolean inProgress;
    private int progressPercentage;
    private Set<Listener> listeners = new HashSet<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    public SyncState(Context context) {
        scheduled = SyncService.isScheduled(context);
    }

    @MainThread
    public void addListener(Listener listener) {
        listeners.add(listener);
        listener.onSyncStateChanged(this);
    }

    @MainThread
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public boolean hasUpdates() {
        return hasUpdates;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void notifyListeners() {
        handler.post(() -> {
            for (Listener listener : listeners) {
                listener.onSyncStateChanged(this);
            }
        });
    }

    void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    void setProgressPercentage(int percentage) {
        this.progressPercentage = percentage;
    }

    void setHasUpdates(boolean hasUpdates) {
        this.hasUpdates = hasUpdates;
    }

    void onSyncFinished() {
        handler.post(() -> {
            for (Listener listener : listeners) {
                listener.onSyncFinished();
            }
        });
    }

    void onSyncFailed() {
        handler.post(() -> {
            for (Listener listener : listeners) {
                listener.onSyncFailed();
            }
        });
    }

    public interface Listener {
        void onSyncStateChanged(SyncState syncState);
        void onSyncFinished();
        void onSyncFailed();
    }
}
