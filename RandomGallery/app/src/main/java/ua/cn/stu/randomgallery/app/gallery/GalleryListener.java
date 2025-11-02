package ua.cn.stu.randomgallery.app.gallery;

import java.util.List;

public interface GalleryListener {
    void onGotGalleryPhotos(List<LocalPhoto> photos);
}
