package ua.cn.stu.randomgallery.app;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import okio.Okio;
import okio.Source;
import ua.cn.stu.randomgallery.app.gallery.GalleryListener;
import ua.cn.stu.randomgallery.app.gallery.LocalPhoto;

public class GalleryRequestHandler extends RequestHandler implements GalleryListener {

    private static final String SCHEME = "gallery";
    private Map<String, LocalPhoto> photos = new TreeMap<>();

    @Override
    public boolean canHandleRequest(Request data) {
        return SCHEME.equals(data.uri.getScheme());
    }

    @Override
    public synchronized Result load(Request request, int networkPolicy) throws IOException {
        String localId = request.uri.getHost();
        LocalPhoto localPhoto = photos.get(localId);
        if (localPhoto == null) {
            throw new FileNotFoundException("Unknown id: " + localId);
        }

        try {
            Source source = Okio.source(localPhoto.openImage());
            return new Result(source, Picasso.LoadedFrom.DISK);
        } catch (Exception e) {
            throw new IOException("Failed to open image", e);
        }
    }

    @Override
    public synchronized void onGotGalleryPhotos(List<LocalPhoto> photos) {
        this.photos.clear();
        for (LocalPhoto localPhoto : photos) {
            this.photos.put(localPhoto.getLocalId(), localPhoto);
        }
    }

    public static String localPhotoToUrl(LocalPhoto localPhoto) {
        return SCHEME + "://" + localPhoto.getLocalId();
    }
}