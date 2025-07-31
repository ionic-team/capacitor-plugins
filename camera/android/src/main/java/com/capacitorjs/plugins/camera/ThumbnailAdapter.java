package com.capacitorjs.plugins.camera;

import static com.capacitorjs.plugins.camera.DeviceUtils.dpToPx;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {

    private final ArrayList<ThumbnailItem> thumbnails;
    private OnThumbnailsChangedCallback thumbnailsChangedCallback = null;
    private OnThumbnailClickListener thumbnailClickListener = null;

    ThumbnailAdapter() {
        this.thumbnails = new ArrayList<>();
    }

    void addThumbnail(Uri uri, Bitmap thumbnail) {
        if (thumbnail == null) return;
        ThumbnailItem item = new ThumbnailItem(uri, thumbnail);
        thumbnails.add(item);
        notifyItemInserted(thumbnails.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int thumbnailSize = dpToPx(context, 80); // Thumbnail size
        int margin = dpToPx(context, 4); // Margin for each side

        FrameLayout frameLayout = new FrameLayout(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(thumbnailSize, thumbnailSize);
        layoutParams.setMargins(margin, margin, margin, margin);
        frameLayout.setLayoutParams(layoutParams);

        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        frameLayout.addView(imageView);

        ImageView removeButton = new ImageView(context);
        int buttonSize = dpToPx(context, 24);
        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(buttonSize, buttonSize);
        buttonParams.gravity = Gravity.TOP | Gravity.END;
        removeButton.setLayoutParams(buttonParams);
        removeButton.setImageResource(R.drawable.ic_cancel_white_24dp);
        frameLayout.addView(removeButton);

        return new ViewHolder(frameLayout, imageView, removeButton);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThumbnailItem item = thumbnails.get(position);
        holder.imageView.setImageBitmap(item.bitmap);

        // Set click listener for the thumbnail
        holder.mainView.setOnClickListener(v -> {
            if (thumbnailClickListener != null) {
                thumbnailClickListener.onThumbnailClick(item.getUri(), item.getBitmap());
            }
        });

        holder.removeButton.setOnClickListener(
            v -> {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    ThumbnailItem removed = thumbnails.remove(currentPosition);

                    notifyItemRemoved(currentPosition);

                    if (thumbnailsChangedCallback != null) {
                        thumbnailsChangedCallback.onThumbnailRemoved(removed.getUri(), removed.getBitmap());
                    }
                }
            }
        );
    }

    @Override
    public int getItemCount() {
        return thumbnails.size();
    }

    public void setOnThumbnailsChangedCallback(OnThumbnailsChangedCallback callback) {
        this.thumbnailsChangedCallback = callback;
    }

    public void setOnThumbnailClickListener(OnThumbnailClickListener listener) {
        this.thumbnailClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView removeButton;
        FrameLayout mainView;

        ViewHolder(@NonNull FrameLayout view, @NonNull ImageView imageView, @NonNull ImageView removeButton) {
            super(view);
            this.imageView = imageView;
            this.mainView = view;
            this.removeButton = removeButton;
        }
    }

    public abstract static class OnThumbnailsChangedCallback {

        public void onThumbnailRemoved(Uri uri, Bitmap bmp) {}
    }

    public interface OnThumbnailClickListener {
        void onThumbnailClick(Uri uri, Bitmap bitmap);
    }

    public static class ThumbnailItem {

        private final Uri uri;
        private final Bitmap bitmap;

        public ThumbnailItem(Uri u, Bitmap bmp) {
            this.uri = u;
            this.bitmap = bmp;
        }

        public Uri getUri() {
            return uri;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
    }
}

