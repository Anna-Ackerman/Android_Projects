package ua.cn.stu.randomgallery.app.screens;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import ua.cn.stu.randomgallery.app.App;
import ua.cn.stu.randomgallery.app.GalleryRequestHandler;
import ua.cn.stu.randomgallery.app.R;
import ua.cn.stu.randomgallery.app.gallery.LocalPhoto;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>
        implements View.OnClickListener {

    private List<LocalPhoto> items = Collections.emptyList();
    private ActionListener actionListener;
    private int imageWidth;

    public GalleryAdapter(int imageWidth, ActionListener listener) {
        this.actionListener = listener;
        this.imageWidth = imageWidth;
    }

    public void submitList(List<LocalPhoto> items) {
        if (this.items.equals(items)) return;
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_photo, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        viewHolder.photoImageView.setOnClickListener(this);
        viewHolder.photoImageView.getLayoutParams().width = imageWidth;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int pos) {
        Context appContext = viewHolder.itemView.getContext().getApplicationContext();
        App app = (App) appContext;
        Picasso picasso = app.getPicasso();

        LocalPhoto localPhoto = items.get(pos);
        viewHolder.nameTextView.setText(localPhoto.getName());
        viewHolder.photoImageView.setTransitionName("item" + pos);

        picasso.load(GalleryRequestHandler.localPhotoToUrl(localPhoto))
                .placeholder(R.color.colorPrimaryDark)
                .into(viewHolder.photoImageView);

        viewHolder.photoImageView.setTag(localPhoto);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(View v) {
        LocalPhoto localPhoto = (LocalPhoto) v.getTag();
        actionListener.onPhotoChosen(v, localPhoto);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        TextView nameTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }

    public interface ActionListener {
        void onPhotoChosen(View source, LocalPhoto photo);
    }
}
