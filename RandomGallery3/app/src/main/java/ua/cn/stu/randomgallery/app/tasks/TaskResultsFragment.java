package ua.cn.stu.randomgallery.app.tasks;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TaskResultsFragment extends Fragment {

    public static final String TAG = TaskResultsFragment.class.getSimpleName();

    public TaskSubject<Boolean> hasUpdatesSubject;
    public TaskSubject<SyncResult> syncSubject;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (hasUpdatesSubject != null) {
            hasUpdatesSubject.cancel();
        }
        if (syncSubject != null) {
            syncSubject.cancel();
        }
    }

    // Клас для результату синхронізації з прогресом
    public static class SyncResult {
        public final boolean success;
        public final int progress;

        public SyncResult(boolean success, int progress) {
            this.success = success;
            this.progress = progress;
        }
    }
}
