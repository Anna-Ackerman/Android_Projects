package ua.cn.stu.randomgallery.app;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import ua.cn.stu.randomgallery.app.screens.DetailsFragment;
import ua.cn.stu.randomgallery.app.screens.GalleryFragment;
import ua.cn.stu.randomgallery.app.screens.Router;
import ua.cn.stu.randomgallery.app.tasks.GalleryTasks;
import ua.cn.stu.randomgallery.app.tasks.Result;
import ua.cn.stu.randomgallery.app.tasks.Status;
import ua.cn.stu.randomgallery.app.tasks.TaskListener;
import ua.cn.stu.randomgallery.app.tasks.TaskManagerFragment;
import ua.cn.stu.randomgallery.app.tasks.TaskResultsFragment;

public class MainActivity extends AppCompatActivity implements Router {

    private App app;
    private TextView messageTextView;
    private ProgressBar syncProgressBar;
    private TextView actionTextView;
    private View messageContainer;

    private TaskManagerFragment taskManagerFragment;
    private TaskResultsFragment taskResultsFragment;
    private GalleryTasks tasks;

    private boolean syncInProgress;
    private boolean hasUpdates;
    private int currentProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (App) getApplicationContext();
        tasks = new GalleryTasks(app.getGalleryClient());

        if (savedInstanceState == null) {
            taskManagerFragment = new TaskManagerFragment();
            taskResultsFragment = new TaskResultsFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmentContainer, new GalleryFragment())
                    .add(taskManagerFragment, TaskManagerFragment.TAG)
                    .add(taskResultsFragment, TaskResultsFragment.TAG)
                    .commit();
        } else {
            taskManagerFragment = (TaskManagerFragment) getSupportFragmentManager()
                    .findFragmentByTag(TaskManagerFragment.TAG);
            taskResultsFragment = (TaskResultsFragment) getSupportFragmentManager()
                    .findFragmentByTag(TaskResultsFragment.TAG);
        }

        actionTextView = findViewById(R.id.actionTextView);
        actionTextView.setOnClickListener(v -> startSync());

        messageTextView = findViewById(R.id.messageTextView);
        messageContainer = findViewById(R.id.messageContainer);
        syncProgressBar = findViewById(R.id.syncProgressBar);

        updateUi();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (taskResultsFragment.hasUpdatesSubject == null) {
            taskResultsFragment.hasUpdatesSubject = taskManagerFragment
                    .submitTask(tasks.createCheckUpdatesTask());
        }
        taskResultsFragment.hasUpdatesSubject.addListener(hasUpdatesListener);

        if (taskResultsFragment.syncSubject != null) {
            taskResultsFragment.syncSubject.addListener(syncListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (taskResultsFragment.hasUpdatesSubject != null) {
            taskResultsFragment.hasUpdatesSubject.removeListener(hasUpdatesListener);
        }

        if (taskResultsFragment.syncSubject != null) {
            taskResultsFragment.syncSubject.removeListener(syncListener);
        }
    }

    private void startSync() {
        if (taskResultsFragment.syncSubject != null) {
            taskResultsFragment.syncSubject.removeListener(syncListener);
        }

        taskResultsFragment.syncSubject = taskManagerFragment.submitTask(
                tasks.createSyncTask(percentage -> {
                    currentProgress = percentage;
                    runOnUiThread(this::updateUi);
                })
        );

        taskResultsFragment.syncSubject.addListener(syncListener);
    }

    private void updateUi() {
        if (syncInProgress) {
            messageContainer.setVisibility(View.VISIBLE);
            messageTextView.setText(getString(R.string.percentage, currentProgress));
            syncProgressBar.setVisibility(View.VISIBLE);
            actionTextView.setVisibility(View.INVISIBLE);
        } else if (hasUpdates) {
            messageContainer.setVisibility(View.VISIBLE);
            messageTextView.setText(R.string.update_available);
            syncProgressBar.setVisibility(View.INVISIBLE);
            actionTextView.setVisibility(View.VISIBLE);
        } else {
            messageContainer.setVisibility(View.GONE);
        }
    }

    private TaskListener<Boolean> hasUpdatesListener = res -> {
        if (res.getStatus() == Status.SUCCESS) {
            this.hasUpdates = res.getData();
            updateUi();
        }
    };

    private TaskListener<TaskResultsFragment.SyncResult> syncListener = res -> {
        this.syncInProgress = res.getStatus() == Status.IN_PROGRESS;

        if (res.getStatus() == Status.SUCCESS) {
            if (res.getData().success) {
                this.hasUpdates = false;
                Toast.makeText(this, R.string.gallery_updated, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.update_error, Toast.LENGTH_SHORT).show();
            }
            this.currentProgress = 0;
        } else if (res.getStatus() == Status.ERROR) {
            Toast.makeText(this, R.string.update_error, Toast.LENGTH_SHORT).show();
            this.currentProgress = 0;
        }

        updateUi();
    };

    @Override
    public void launchDetails(View sharedView, String localPhotoId) {
        Fragment fragment = DetailsFragment.newInstance(localPhotoId);

        TransitionSet transitionSet = new TransitionSet()
                .setOrdering(TransitionSet.ORDERING_TOGETHER)
                .addTransition(new ChangeBounds())
                .addTransition(new ChangeTransform())
                .addTransition(new ChangeImageTransform());

        fragment.setSharedElementEnterTransition(transitionSet);
        fragment.setSharedElementReturnTransition(transitionSet);

        fragment.setEnterTransition(new Fade());
        getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer)
                .setReturnTransition(new Fade());

        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(sharedView, getString(R.string.shared_tag))
                .addToBackStack(null)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    public void back() {
        onBackPressed();
    }
}