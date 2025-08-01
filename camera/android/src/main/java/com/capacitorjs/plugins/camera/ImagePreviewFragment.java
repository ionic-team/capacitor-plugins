package com.capacitorjs.plugins.camera;

import android.content.res.Configuration;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A DialogFragment that displays a full-screen preview of an image.
 * This is shown when a user taps on a thumbnail in the camera interface.
 */
public class ImagePreviewFragment extends DialogFragment {

    private List<Uri> imageUris;
    private int currentPosition;
    private ViewPager2 viewPager;
    private TextView positionIndicator;

    /**
     * Create a new instance of ImagePreviewFragment with the provided image URI
     *
     * @param uri The URI of the image to display in the preview
     * @return A new instance of ImagePreviewFragment
     */
    public static ImagePreviewFragment newInstance(Uri uri) {
        List<Uri> singleImageList = new ArrayList<>();
        singleImageList.add(uri);
        return newInstance(singleImageList, 0);
    }

    /**
     * Create a new instance of ImagePreviewFragment with a list of images and starting position
     *
     * @param imageUris List of image URIs to display
     * @param position The starting position in the list
     * @return A new instance of ImagePreviewFragment
     */
    public static ImagePreviewFragment newInstance(List<Uri> imageUris, int position) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        fragment.imageUris = new ArrayList<>(imageUris);
        fragment.currentPosition = position;
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

        // Create ViewPager2 for swipe navigation
        viewPager = new ViewPager2(requireContext());
        viewPager.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        
        // Set up the adapter
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, imageUris);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition, false);
        
        rootLayout.addView(viewPager);

        // Create position indicator (only show if more than one image)
        if (imageUris.size() > 1) {
            positionIndicator = new TextView(requireContext());
            positionIndicator.setTextColor(Color.WHITE);
            positionIndicator.setTextSize(16);
            
            // Create pill-shaped background
            GradientDrawable pillBackground = new GradientDrawable();
            pillBackground.setShape(GradientDrawable.RECTANGLE);
            pillBackground.setColor(0x80000000); // Semi-transparent black
            pillBackground.setCornerRadius(dpToPx(requireContext(), 20)); // Large corner radius for pill shape
            positionIndicator.setBackground(pillBackground);
            
            positionIndicator.setPadding(dpToPx(requireContext(), 16), dpToPx(requireContext(), 8), 
                                       dpToPx(requireContext(), 16), dpToPx(requireContext(), 8));
            
            FrameLayout.LayoutParams indicatorParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            indicatorParams.gravity = android.view.Gravity.TOP | android.view.Gravity.CENTER_HORIZONTAL;
            indicatorParams.setMargins(0, dpToPx(requireContext(), 60), 0, 0);
            positionIndicator.setLayoutParams(indicatorParams);
            
            updatePositionIndicator(currentPosition);
            rootLayout.addView(positionIndicator);
            
            // Listen for page changes
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    updatePositionIndicator(position);
                }
            });
        }

        // Create the close button with matching CameraFragment styling
        FloatingActionButton closeButton = new FloatingActionButton(requireContext());
        closeButton.setImageResource(R.drawable.close_24px);
        closeButton.setBackgroundTintList(createButtonColorList());
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
    
    private void updatePositionIndicator(int position) {
        if (positionIndicator != null) {
            positionIndicator.setText((position + 1) + " / " + imageUris.size());
        }
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

                    // Additional rotation for landscape mode if needed
                    int orientation = requireContext().getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        // In landscape mode, ensure the image is properly oriented
                        // The image is already rotated based on EXIF data, so we don't need additional rotation
                        // But we ensure it's displayed with the correct aspect ratio
                    }
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
    
    @NonNull
    private static ColorStateList createButtonColorList() {
        int[][] states = new int[][] {
            new int[] { android.R.attr.state_enabled }, // enabled
            new int[] { -android.R.attr.state_enabled }, // disabled
            new int[] { -android.R.attr.state_checked }, // unchecked
            new int[] { android.R.attr.state_pressed } // pressed
        };

        int[] colors = new int[] { Color.DKGRAY, Color.TRANSPARENT, Color.TRANSPARENT, Color.LTGRAY };
        return new ColorStateList(states, colors);
    }
    
    /**
     * Adapter for ViewPager2 to handle image swiping
     */
    private static class ImagePagerAdapter extends FragmentStateAdapter {
        private final List<Uri> imageUris;
        
        public ImagePagerAdapter(@NonNull Fragment fragment, List<Uri> imageUris) {
            super(fragment);
            this.imageUris = imageUris;
        }
        
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return ImagePageFragment.newInstance(imageUris.get(position));
        }
        
        @Override
        public int getItemCount() {
            return imageUris.size();
        }
    }
    
    /**
     * Fragment for individual image pages in the ViewPager2
     */
    public static class ImagePageFragment extends Fragment {
        private Uri imageUri;
        
        public static ImagePageFragment newInstance(Uri uri) {
            ImagePageFragment fragment = new ImagePageFragment();
            fragment.imageUri = uri;
            return fragment;
        }
        
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Create the layout for a single image
            FrameLayout frameLayout = new FrameLayout(requireContext());
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            
            // Create the image view
            ImageView imageView = new ImageView(requireContext());
            imageView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            
            // Create a progress bar
            ProgressBar progressBar = new ProgressBar(requireContext());
            FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            progressParams.gravity = android.view.Gravity.CENTER;
            progressBar.setLayoutParams(progressParams);
            
            frameLayout.addView(imageView);
            frameLayout.addView(progressBar);
            
            // Load the image
            loadFullResolutionImage(imageUri, imageView, progressBar);
            
            return frameLayout;
        }
        
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

                        // Additional rotation for landscape mode if needed
                        int orientation = requireContext().getResources().getConfiguration().orientation;
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            // In landscape mode, ensure the image is properly oriented
                            // The image is already rotated based on EXIF data, so we don't need additional rotation
                            // But we ensure it's displayed with the correct aspect ratio
                        }
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
    }
}