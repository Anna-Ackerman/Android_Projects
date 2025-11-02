package ua.cn.stu.randomgallery.app;

import android.content.Intent;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import ua.cn.stu.randomgallery.app.screens.DetailsFragment;
import ua.cn.stu.randomgallery.app.screens.GalleryFragment;
import ua.cn.stu.randomgallery.app.screens.Router;
import ua.cn.stu.randomgallery.app.sync.ActionsHandlerService;
import ua.cn.stu.randomgallery.app.sync.SyncService;
import ua.cn.stu.randomgallery.app.sync.SyncState;

public class MainActivity extends AppCompatActivity
        implements SyncState.Listener, Router {

    private App app;
    private TextView messageTextView;
    private TextView progressTextView;
    private TextView actionTextView;
    private View messageContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (App) getApplicationContext();

        if (savedInstanceState == null) {
            // Чистий запуск, немає фрагментів
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmentContainer, new GalleryFragment())
                    .commit();
        }

        // Перевіряємо оновлення при старті
        Intent checkForUpdatesIntent = new Intent(this, ActionsHandlerService.class);
        checkForUpdatesIntent.setAction(ActionsHandlerService.ACTION_CHECK_FOR_UPDATES);
        startService(checkForUpdatesIntent);

        // Ініціалізуємо UI елементи
        actionTextView = findViewById(R.id.actionTextView);
        actionTextView.setOnClickListener(v -> {
            SyncService.scheduleUpdate(this);
        });

        messageTextView = findViewById(R.id.messageTextView);
        messageContainer = findViewById(R.id.messageContainer);
        progressTextView = findViewById(R.id.progressTextView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        app.getSyncState().addListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        app.getSyncState().removeListener(this);
    }

    @Override
    public void onSyncStateChanged(SyncState syncState) {
        if (syncState.hasUpdates() || syncState.isScheduled() || syncState.isInProgress()) {
            messageContainer.setVisibility(View.VISIBLE);

            if (syncState.isInProgress()) {
                messageTextView.setText(R.string.updating_gallery);
                progressTextView.setVisibility(View.VISIBLE);
                progressTextView.setText(getString(R.string.percentage, syncState.getProgressPercentage()));
                actionTextView.setVisibility(View.INVISIBLE);
            } else if (syncState.isScheduled()) {
                messageTextView.setText(R.string.updating_scheduled);
                progressTextView.setVisibility(View.INVISIBLE);
                actionTextView.setVisibility(View.INVISIBLE);
            } else {
                messageTextView.setText(R.string.update_available);
                progressTextView.setVisibility(View.INVISIBLE);
                actionTextView.setVisibility(View.VISIBLE);
            }
        } else {
            messageContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSyncFinished() {
        Toast.makeText(this, R.string.gallery_updated, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSyncFailed() {
        Toast.makeText(this, R.string.update_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void launchDetails(View sharedView, String localPhotoId) {
        Fragment fragment = DetailsFragment.newInstance(localPhotoId);

        // Transition для shared image
        TransitionSet transitionSet = new TransitionSet()
                .setOrdering(TransitionSet.ORDERING_TOGETHER)
                .addTransition(new ChangeBounds())
                .addTransition(new ChangeTransform())
                .addTransition(new ChangeImageTransform());

        fragment.setSharedElementEnterTransition(transitionSet);
        fragment.setSharedElementReturnTransition(transitionSet);

        // Transitions для фрагментів
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
