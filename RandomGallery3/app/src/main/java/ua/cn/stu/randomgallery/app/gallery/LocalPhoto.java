package ua.cn.stu.randomgallery.app.gallery;

import java.io.InputStream;

public class LocalPhoto {
    private final String localId;
    private final String name;
    private final PhotoProvider photoProvider;

    public LocalPhoto(String localId, String name, PhotoProvider photoProvider) {
        this.localId = localId;
        this.name = name;
        this.photoProvider = photoProvider;
    }

    public String getLocalId() {
        return localId;
    }

    public String getName() {
        return name;
    }

    public InputStream openImage() throws Exception {
        return photoProvider.openImage(localId);
    }

    public interface PhotoProvider {
        InputStream openImage(String localId) throws Exception;
    }
}