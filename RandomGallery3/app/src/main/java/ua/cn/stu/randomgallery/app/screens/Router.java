package ua.cn.stu.randomgallery.app.screens;

import android.view.View;

public interface Router {
    void launchDetails(View sharedView, String localPhotoId);
    void back();
}
