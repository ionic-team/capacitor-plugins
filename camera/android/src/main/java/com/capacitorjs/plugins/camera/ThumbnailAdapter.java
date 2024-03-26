package com.capacitorjs.plugins.camera;

import static com.capacitorjs.plugins.camera.DeviceUtils.dpToPx;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {
    private final ArrayList<ThumbnailItem> thumbnails;
    private OnThumbnailsChangedCallback thumbnailsChangedCallback = null;

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
        int thumbnailPx = dpToPx(context, 100); // Convert dp to pixels
        int buttonSize = dpToPx(context, 24);

        // Create the ConstraintLayout as a container
        ConstraintLayout constraintLayout = new ConstraintLayout(context);
        int extraSpaceForButton = dpToPx(context, 24); // Example extra space in dp
        ConstraintLayout.LayoutParams clLayoutParams = new ConstraintLayout.LayoutParams(
                thumbnailPx + extraSpaceForButton,
                thumbnailPx + extraSpaceForButton
        );
        constraintLayout.setLayoutParams(clLayoutParams);

        ImageView imageView = new ImageView(context);
        imageView.setId(View.generateViewId());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ConstraintLayout.LayoutParams imageParams = new ConstraintLayout.LayoutParams(thumbnailPx, thumbnailPx);
        // Set imageView to be centered in the ConstraintLayout
        imageParams.startToStart = ConstraintSet.PARENT_ID;
        imageParams.topToTop = ConstraintSet.PARENT_ID;
        imageParams.endToEnd = ConstraintSet.PARENT_ID;
        imageParams.bottomToBottom = ConstraintSet.PARENT_ID;
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        constraintLayout.addView(imageView);

        FloatingActionButton removeButton = new FloatingActionButton(context);
        removeButton.setCustomSize(buttonSize);
        removeButton.setId(View.generateViewId());
        // Set the icon for the remove button
        Bitmap cutoutBitmap = createCutoutBitmap(buttonSize);
        Drawable cutoutDrawable = new BitmapDrawable(context.getResources(), cutoutBitmap);
        removeButton.setImageDrawable(cutoutDrawable);
        // Set the background color of the button to white
        ColorStateList whiteBackground = ColorStateList.valueOf(Color.TRANSPARENT);
        removeButton.setBackgroundTintList(whiteBackground);
        ConstraintLayout.LayoutParams buttonParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        // Align button to the top and end of the ConstraintLayout
        buttonParams.topToTop = ConstraintSet.PARENT_ID;
        buttonParams.endToEnd = ConstraintSet.PARENT_ID;
        removeButton.setLayoutParams(buttonParams);
        constraintLayout.addView(removeButton);

        // Apply constraints to position the views correctly using ConstraintSet
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);
        set.connect(imageView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        set.connect(imageView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        set.connect(imageView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        set.connect(imageView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

        set.connect(removeButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        set.connect(removeButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);

        // Offset the button to make it overlap the corner of the ImageView
        set.setMargin(removeButton.getId(), ConstraintSet.END, -buttonSize / 2);
        set.setMargin(removeButton.getId(), ConstraintSet.TOP, -buttonSize / 2);

        set.applyTo(constraintLayout);
        return new ViewHolder(constraintLayout, imageView, removeButton);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageBitmap(thumbnails.get(position).bitmap);

        holder.removeButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                ThumbnailItem removed = thumbnails.remove(currentPosition);

                notifyItemRemoved(currentPosition);

                if (thumbnailsChangedCallback != null) {
                    thumbnailsChangedCallback.onThumbnailRemoved(removed.getUri(), removed.getBitmap());
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return thumbnails.size();
    }

    public void setOnThumbnailsChangedCallback(OnThumbnailsChangedCallback callback) {
        this.thumbnailsChangedCallback = callback;
    }


    private Bitmap createCutoutBitmap(int diameter) {
        Bitmap bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw the white circular background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint);

        paint.setColor(Color.TRANSPARENT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setStrokeWidth(4); // Set the stroke width for the 'X'

        // Coordinates for the 'X'
        float startX1 = diameter * 0.25f; // Start X for the first line
        float endX1 = diameter * 0.75f; // End X for the first line
        float startY1 = diameter * 0.25f; // Start Y for the first line
        float endY1 = diameter * 0.75f; // End Y for the first line

        float startX2 = diameter * 0.75f; // Start X for the second line
        float endX2 = diameter * 0.25f; // End X for the second line
        float startY2 = diameter * 0.25f; // Start Y for the second line
        float endY2 = diameter * 0.75f; // End Y for the second line

        canvas.drawLine(startX1, startY1, endX1, endY1, paint); // First line of 'X'
        canvas.drawLine(startX2, startY2, endX2, endY2, paint); // Second line of 'X'

        return bitmap;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        FloatingActionButton removeButton;
        ConstraintLayout mainView;

        ViewHolder(@NonNull ConstraintLayout view, @NonNull ImageView imageView, @NonNull FloatingActionButton removeButton) {
            super(view);
            this.imageView = imageView;
            this.mainView = view;
            this.removeButton = removeButton;
        }
    }

    public static abstract class OnThumbnailsChangedCallback {
        public void onThumbnailRemoved(Uri uri, Bitmap bmp) {
        }
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
