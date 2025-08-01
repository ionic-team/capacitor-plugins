package com.capacitorjs.plugins.camera;

import static com.capacitorjs.plugins.camera.DeviceUtils.dpToPx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
        ThumbnailItem item = new ThumbnailItem(uri, thumbnail, false);
        thumbnails.add(item);
        notifyItemInserted(thumbnails.size() - 1);
    }

    void addLoadingThumbnail() {
        ThumbnailItem item = new ThumbnailItem(null, null, true);
        thumbnails.add(item);
        notifyItemInserted(thumbnails.size() - 1);
    }

    void replaceLoadingThumbnail(Uri uri, Bitmap thumbnail) {
        // Find the loading thumbnail (should be the last one)
        for (int i = thumbnails.size() - 1; i >= 0; i--) {
            ThumbnailItem item = thumbnails.get(i);
            if (item.isLoading()) {
                thumbnails.set(i, new ThumbnailItem(uri, thumbnail, false));
                notifyItemChanged(i);
                break;
            }
        }
    }

    void removeLoadingThumbnails() {
        // Remove all loading thumbnails from the end backwards to avoid index issues
        for (int i = thumbnails.size() - 1; i >= 0; i--) {
            ThumbnailItem item = thumbnails.get(i);
            if (item.isLoading()) {
                thumbnails.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    boolean hasLoadingThumbnails() {
        for (ThumbnailItem item : thumbnails) {
            if (item.isLoading()) {
                return true;
            }
        }
        return false;
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

        // Add progress bar for loading state
        ProgressBar progressBar = new ProgressBar(context);
        int progressSize = dpToPx(context, 32);
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(progressSize, progressSize);
        progressParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(progressParams);
        progressBar.setVisibility(View.GONE); // Initially hidden
        frameLayout.addView(progressBar);

        return new ViewHolder(frameLayout, imageView, removeButton, progressBar);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThumbnailItem item = thumbnails.get(position);
        
        if (item.isLoading()) {
            // Show loading state
            holder.imageView.setImageBitmap(null);
            holder.imageView.setBackgroundColor(Color.GRAY);
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.removeButton.setVisibility(View.GONE);
            holder.mainView.setOnClickListener(null); // Disable clicks for loading items
        } else {
            // Show actual thumbnail
            holder.imageView.setImageBitmap(item.bitmap);
            holder.imageView.setBackgroundColor(Color.TRANSPARENT);
            holder.progressBar.setVisibility(View.GONE);
            holder.removeButton.setVisibility(View.VISIBLE);
            
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
    }

    @Override
    public int getItemCount() {
        return thumbnails.size();
    }

    /**
     * Get the ThumbnailItem at the specified position
     *
     * @param position Position of the item to retrieve
     * @return The ThumbnailItem at the specified position, or null if position is invalid
     */
    public ThumbnailItem getThumbnailItem(int position) {
        if (position >= 0 && position < thumbnails.size()) {
            return thumbnails.get(position);
        }
        return null;
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
        ProgressBar progressBar;

        ViewHolder(@NonNull FrameLayout view, @NonNull ImageView imageView, @NonNull ImageView removeButton, @NonNull ProgressBar progressBar) {
            super(view);
            this.imageView = imageView;
            this.mainView = view;
            this.removeButton = removeButton;
            this.progressBar = progressBar;
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
        private final boolean loading;

        public ThumbnailItem(Uri u, Bitmap bmp, boolean isLoading) {
            this.uri = u;
            this.bitmap = bmp;
            this.loading = isLoading;
        }

        public Uri getUri() {
            return uri;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
        
        public boolean isLoading() {
            return loading;
        }
    }
}

