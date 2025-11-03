package ua.cn.stu.randomgallery.app.screens;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ua.cn.stu.randomgallery.app.App;
import ua.cn.stu.randomgallery.app.R;
import ua.cn.stu.randomgallery.app.gallery.GalleryClient;
import ua.cn.stu.randomgallery.app.gallery.GalleryListener;
import ua.cn.stu.randomgallery.app.gallery.LocalPhoto;

public class GalleryFragment extends Fragment implements GalleryListener {

    private RecyclerView galleryRecyclerView;
    private TextView emptyTextView;
    private GalleryAdapter adapter;
    private Router router;
    private GalleryClient galleryClient;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        App app = (App) context.getApplicationContext();
        galleryClient = app.getGalleryClient();  // ← тепер MockGalleryClient
        router = (Router) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        galleryRecyclerView = view.findViewById(R.id.galleryRecyclerView);
        emptyTextView = view.findViewById(R.id.emptyTextView);

        boolean isPortrait = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
        int countPerRow = isPortrait ? 2 : 3;

        adapter = new GalleryAdapter(
                getItemWidth(countPerRow),
                (source, photo) -> {
                    router.launchDetails(source, photo.getLocalId());
                }
        );

        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(), countPerRow);
        galleryRecyclerView.setLayoutManager(manager);
        galleryRecyclerView.addItemDecoration(new SpacesItemDecoration(countPerRow));
        galleryClient.addListener(this);
        galleryRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        galleryClient.removeListener(this);
    }

    @Override
    public void onGotGalleryPhotos(List<LocalPhoto> photos) {
        MainThreadHandler.run(() -> {
            adapter.submitList(photos);
            if (photos.isEmpty()) {
                emptyTextView.setVisibility(View.VISIBLE);
                galleryRecyclerView.setVisibility(View.GONE);
            } else {
                emptyTextView.setVisibility(View.GONE);
                galleryRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    private int getItemWidth(int countPerRow) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int imageMargin = getResources().getDimensionPixelSize(R.dimen.image_margin);
        return (screenWidth - imageMargin * 2) / countPerRow - imageMargin * 2;
    }

    static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int countPerRow;

        SpacesItemDecoration(int countPerRow) {
            this.countPerRow = countPerRow;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect,
                                   @NonNull View view,
                                   @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position < countPerRow) {
                outRect.top = view.getResources()
                        .getDimensionPixelSize(R.dimen.additional_top_offset);
            } else {
                outRect.top = 0;
            }
        }
    }
}