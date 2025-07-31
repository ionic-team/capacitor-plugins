package com.capacitorjs.plugins.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.IOException;
import java.io.InputStream;

/**
 * A DialogFragment that displays a full-screen preview of an image.
 * This is shown when a user taps on a thumbnail in the camera interface.
 */
public class ImagePreviewFragment extends DialogFragment {

    private Uri imageUri;

    /**
     * Create a new instance of ImagePreviewFragment with the provided image URI
     *
     * @param uri The URI of the image to display in the preview
     * @return A new instance of ImagePreviewFragment
     */
    public static ImagePreviewFragment newInstance(Uri uri) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        fragment.imageUri = uri;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Create the main layout
        FrameLayout rootLayout = new FrameLayout(requireContext());
        rootLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.setBackgroundColor(Color.BLACK);

        // Create the image view
        ImageView imageView = new ImageView(requireContext());
        imageView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // Create a progress bar to show while loading
        ProgressBar progressBar = new ProgressBar(requireContext());
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        progressParams.gravity = android.view.Gravity.CENTER;
        progressBar.setLayoutParams(progressParams);

        rootLayout.addView(imageView);
        rootLayout.addView(progressBar);

        // Load the full-resolution image
        loadFullResolutionImage(imageUri, imageView, progressBar);

        // Create the close button
        FloatingActionButton closeButton = new FloatingActionButton(requireContext());
        closeButton.setImageResource(R.drawable.close_24px);
        closeButton.setColorFilter(Color.WHITE);
        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(dpToPx(requireContext(), 20), dpToPx(requireContext(), 40), 0, 0);
        closeButton.setLayoutParams(buttonParams);
        closeButton.setOnClickListener(v -> dismiss());
        rootLayout.addView(closeButton);

        return rootLayout;
    }

    /**
     * Loads the full-resolution image from the given URI
     *
     * @param uri The URI of the image to load
     * @param imageView The ImageView to display the image in
     * @param progressBar The ProgressBar to show while loading
     */
    private void loadFullResolutionImage(Uri uri, ImageView imageView, ProgressBar progressBar) {
        new Thread(() -> {
            try {
                // Show progress bar while loading
                requireActivity().runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

                // Load the full-resolution image
                InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                Bitmap fullResolutionBitmap = BitmapFactory.decodeStream(inputStream);
                if (inputStream != null) {
                    inputStream.close();
                }

                // Get EXIF data and correct orientation
                ExifWrapper exifWrapper = ImageUtils.getExifData(requireContext(), fullResolutionBitmap, uri);
                try {
                    fullResolutionBitmap = ImageUtils.correctOrientation(requireContext(), fullResolutionBitmap, uri, exifWrapper);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Use final reference for the bitmap to use in the UI thread
                final Bitmap correctedBitmap = fullResolutionBitmap;

                // Update UI on main thread
                requireActivity().runOnUiThread(() -> {
                    imageView.setImageBitmap(correctedBitmap);
                    progressBar.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                });
            }
        }).start();
    }

    private int dpToPx(android.content.Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}