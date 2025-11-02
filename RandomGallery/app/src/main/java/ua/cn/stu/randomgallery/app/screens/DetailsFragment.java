package ua.cn.stu.randomgallery.app.screens;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.util.List;

import ua.cn.stu.randomgallery.app.App;
import ua.cn.stu.randomgallery.app.GalleryRequestHandler;
import ua.cn.stu.randomgallery.app.R;
import ua.cn.stu.randomgallery.app.gallery.GalleryClient;
import ua.cn.stu.randomgallery.app.gallery.GalleryListener;
import ua.cn.stu.randomgallery.app.gallery.LocalPhoto;

public class DetailsFragment extends Fragment implements GalleryListener {

    private static final String ARG_LOCAL_PHOTO_ID = "LOCAL_PHOTO_ID";

    private ImageView photoImageView;
    private TextView titleTextView;
    private GalleryClient client;
    private Router router;
    private Picasso picasso;

    public static DetailsFragment newInstance(String localPhotoId) {
        Bundle args = new Bundle();
        args.putString(ARG_LOCAL_PHOTO_ID, localPhotoId);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        App app = (App) context.getApplicationContext();
        client = app.getGalleryClient();
        picasso = app.getPicasso();
        router = (Router) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleTextView = view.findViewById(R.id.titleTextView);
        photoImageView = view.findViewById(R.id.photoImageView);

        client.addListener(this);
        titleTextView.postDelayed(delayedTitle, 300);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        client.removeListener(this);
        titleTextView.removeCallbacks(delayedTitle);
        titleTextView.setVisibility(View.GONE);
    }

    @Override
    public void onGotGalleryPhotos(List<LocalPhoto> photos) {
        MainThreadHandler.run(() -> {
            String photoId = getArguments().getString(ARG_LOCAL_PHOTO_ID);
            LocalPhoto photo = find(photos, photoId);

            if (photo != null) {
                picasso.load(GalleryRequestHandler.localPhotoToUrl(photo))
                        .placeholder(R.color.colorPrimaryDark)
                        .into(photoImageView);
                titleTextView.setText(photo.getName());
            } else {
                router.back();
            }
        });
    }

    @Nullable
    private LocalPhoto find(List<LocalPhoto> photos, String id) {
        for (LocalPhoto photo : photos) {
            if (photo.getLocalId().equals(id)) return photo;
        }
        return null;
    }

    private Runnable delayedTitle = () -> {
        titleTextView.setAlpha(0);
        titleTextView.setVisibility(View.VISIBLE);
        titleTextView.animate().alpha(1).start();
    };
}
