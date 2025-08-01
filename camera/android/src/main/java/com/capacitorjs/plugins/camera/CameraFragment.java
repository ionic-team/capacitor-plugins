package com.capacitorjs.plugins.camera;

import static com.capacitorjs.plugins.camera.DeviceUtils.dpToPx;

import android.view.ViewTreeObserver;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ZoomState;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import androidx.collection.LruCache;

@SuppressWarnings("FieldCanBeLocal")
public class CameraFragment extends Fragment {

    // Constants
    @SuppressWarnings("unused")
    private final String TAG = "CameraFragment";
    private final String FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS";
    private final String PHOTO_TYPE = "image/jpeg";

    private final String CONFIRM_CANCEL_MESSAGE = "Are you sure?";
    private final String CONFIRM_CANCEL_POSITIVE = "Yes";
    private final String CONFIRM_CANCEL_NEGATIVE = "No";

    @ColorInt
    private final int ZOOM_TAB_LAYOUT_BACKGROUND_COLOR = 0x80000000;

    @ColorInt
    private final int ZOOM_BUTTON_COLOR_SELECTED = 0xFFFFFFFF;

    @ColorInt
    private final int ZOOM_BUTTON_COLOR_UNSELECTED = 0x80FFFFFF;

    private final AtomicBoolean isSnappingZoom = new AtomicBoolean(false);
    // View related variables
    private RelativeLayout relativeLayout;
    private RelativeLayout bottomBar;
    private PreviewView previewView;
    private ImageView focusIndicator;
    private ThumbnailAdapter thumbnailAdapter;
    private RecyclerView filmstripView;
    private TabLayout zoomTabLayout;
    private CardView zoomTabCardView;
    private FloatingActionButton takePictureButton;
    private FloatingActionButton flipCameraButton;
    private FloatingActionButton doneButton;
    private FloatingActionButton closeButton;
    private FloatingActionButton flashButton;
    private RelativeLayout.LayoutParams bottomBarLayoutParams;
    private RelativeLayout.LayoutParams cardViewLayoutParams;
    private RelativeLayout.LayoutParams tabLayoutParams;
    private RelativeLayout.LayoutParams takePictureLayoutParams;
    private RelativeLayout.LayoutParams flipButtonLayoutParams;
    private RelativeLayout.LayoutParams doneButtonLayoutParams;
    private RelativeLayout.LayoutParams closeButtonLayoutParams;
    private RelativeLayout.LayoutParams flashButtonLayoutParams;
    private DisplayMetrics displayMetrics;
    private boolean isLandscape = false;
    private RelativeLayout controlsContainer; // Container for camera controls in landscape mode
    // Camera variables
    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private int flashMode = ImageCapture.FLASH_MODE_AUTO;

    @SuppressWarnings("unused")
    private ZoomState zoomRatio = null;

    private float minZoom = 0f;

    @SuppressWarnings("unused")
    private float maxZoom = 1f;

    private ExecutorService cameraExecutor;
    private LifecycleCameraController cameraController;
    // Utility variables
    private LruCache<Uri, Bitmap> imageCache;
    private ArrayList<ZoomTab> zoomTabs;

    private Handler zoomHandler = null;
    private Runnable zoomRunnable = null;
    private MediaActionSound mediaActionSound;

    // Callbacks
    private OnImagesCapturedCallback imagesCapturedCallback;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calculate cache size as 1/8th of available memory
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        // Initialize LruCache for image memory management
        imageCache = new LruCache<Uri, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Uri key, Bitmap bitmap) {
                // Size in kilobytes
                return bitmap.getByteCount() / 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, Uri key, Bitmap oldValue, Bitmap newValue) {
                if (evicted && oldValue != null && !oldValue.isRecycled()) {
                    // Recycle bitmap to free memory immediately when evicted from cache
                    oldValue.recycle();
                }
            }
        };

        zoomTabs = new ArrayList<>();
        zoomHandler = new Handler(requireActivity().getMainLooper());
        mediaActionSound = new MediaActionSound();
        mediaActionSound.load(MediaActionSound.SHUTTER_CLICK);

        // Register for configuration changes (like orientation changes)
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Restore the original system UI settings
        Window window = requireActivity().getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.show(android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT);
            }
        } else {
            View decorView = window.getDecorView();
            int flags = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(flags);
        }
        window.setStatusBarColor(requireActivity().getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(requireActivity().getResources().getColor(android.R.color.transparent));

        // Clean up any ViewTreeObserver listeners that might still be active
        cleanupViewTreeObservers();

        // Clear image cache to free memory
        if (imageCache != null) {
            imageCache.evictAll();
            imageCache = null;
        }

        if (mediaActionSound != null) {
            mediaActionSound.release();
            mediaActionSound = null;
        }

        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
            try {
                // Wait for tasks to complete with timeout
                if (!cameraExecutor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    cameraExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                cameraExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Cleans up any ViewTreeObserver listeners to prevent memory leaks
     */
    private void cleanupViewTreeObservers() {
        try {
            // Clean up ViewTreeObserver listeners for all views that might have them
            if (relativeLayout != null && relativeLayout.getViewTreeObserver().isAlive()) {
                // Use a no-op listener to avoid crashes when removing unknown listeners
                ViewTreeObserver.OnGlobalLayoutListener noOpListener = () -> {};
                relativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(noOpListener);
            }

            if (previewView != null && previewView.getViewTreeObserver().isAlive()) {
                ViewTreeObserver.OnGlobalLayoutListener noOpListener = () -> {};
                previewView.getViewTreeObserver().removeOnGlobalLayoutListener(noOpListener);
            }

            if (zoomTabCardView != null && zoomTabCardView.getViewTreeObserver().isAlive()) {
                ViewTreeObserver.OnGlobalLayoutListener noOpListener = () -> {};
                zoomTabCardView.getViewTreeObserver().removeOnGlobalLayoutListener(noOpListener);
            }

            if (filmstripView != null && filmstripView.getViewTreeObserver().isAlive()) {
                ViewTreeObserver.OnGlobalLayoutListener noOpListener = () -> {};
                filmstripView.getViewTreeObserver().removeOnGlobalLayoutListener(noOpListener);
            }
        } catch (Exception e) {
            // Log but don't crash if there's an issue cleaning up listeners
            Log.e(TAG, "Error cleaning up ViewTreeObserver listeners", e);
        }
    }

    @Nullable
    /**
     * Checks if the device is currently in landscape mode
     *
     * @param context The context to check orientation
     * @return true if in landscape mode, false otherwise
     */
    private boolean isLandscapeMode(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Check if device is in landscape mode with the new configuration
        boolean wasLandscape = isLandscape;
        isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;

        // Recreate the layout when orientation changes
        if (relativeLayout != null) {
            // Save the current camera state
            int currentLensFacing = lensFacing;
            int currentFlashMode = flashMode;

            // Completely recreate the camera controller when switching orientations
            if (cameraController != null) {
                cameraController.unbind();
                cameraController = null;
            }

            // Remove all views
            relativeLayout.removeAllViews();

            // Recreate the UI with the new orientation
            FragmentActivity fragmentActivity = requireActivity();
            int margin = (int) (20 * displayMetrics.density);
            int barHeight = (int) (100 * displayMetrics.density);

            ColorStateList buttonColors = createButtonColorList();

            // Create a black background that fills the entire screen
            View blackBackground = new View(fragmentActivity);
            blackBackground.setId(View.generateViewId());
            blackBackground.setBackgroundColor(Color.BLACK);
            RelativeLayout.LayoutParams blackBgParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            );
            blackBackground.setLayoutParams(blackBgParams);
            relativeLayout.addView(blackBackground, 0); // Add at index 0 to be behind everything

            // Create the preview view first
            createPreviewView(fragmentActivity);

            createFocusIndicator(fragmentActivity);

            if (isLandscape) {
                // In landscape mode, create a container for controls on the right side
                createControlsContainerForLandscape(fragmentActivity, buttonColors, margin);
            } else {
                // In portrait mode, create the bottom bar and buttons
                createBottomBar(fragmentActivity, barHeight, margin, buttonColors);

                // Set preview view to be above bottom bar in portrait mode
                RelativeLayout.LayoutParams previewParams = (RelativeLayout.LayoutParams) previewView.getLayoutParams();
                previewParams.addRule(RelativeLayout.ABOVE, bottomBar.getId());
                previewView.setLayoutParams(previewParams);

                // Zoom bar is above the bottom bar/buttons
                createZoomTabLayout(fragmentActivity, margin);

                // Thumbnail images in the filmstrip are above the zoom buttons
                createFilmstripView(fragmentActivity);

                // Close button and flash are top left/right corners
                createCloseButton(fragmentActivity, margin, buttonColors);
                createFlashButton(fragmentActivity, margin, buttonColors);
            }

            // Create a new camera controller
            cameraController = new LifecycleCameraController(requireActivity());
            cameraController.bindToLifecycle(requireActivity());
            previewView.setController(cameraController);

            // Restore camera settings
            lensFacing = currentLensFacing;
            flashMode = currentFlashMode;

            // Make sure the camera selector is set correctly
            CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
            cameraController.setCameraSelector(cameraSelector);
            cameraController.setImageCaptureFlashMode(flashMode);

            // Force layout update
            relativeLayout.requestLayout();
            relativeLayout.invalidate();
            previewView.requestLayout();

            // Use ViewTreeObserver to efficiently handle layout updates
            relativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // Remove the listener to prevent multiple callbacks
                    relativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    if (isLandscape) {
                        // Force the preview view to take up the correct width in landscape mode
                        int containerWidth = (int) (displayMetrics.widthPixels * 0.2);
                        RelativeLayout.LayoutParams previewParams = (RelativeLayout.LayoutParams) previewView.getLayoutParams();

                        // Clear any existing rules that might be interfering
                        previewParams.removeRule(RelativeLayout.ABOVE);
                        previewParams.removeRule(RelativeLayout.BELOW);
                        previewParams.removeRule(RelativeLayout.RIGHT_OF);
                        previewParams.removeRule(RelativeLayout.LEFT_OF);
                        previewParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
                        previewParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                        previewParams.removeRule(RelativeLayout.CENTER_VERTICAL);

                        // Set explicit width to 80% of screen width
                        previewParams.width = displayMetrics.widthPixels - containerWidth;
                        previewParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;

                        // Set the correct rules for landscape mode
                        previewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        previewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        previewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        if (controlsContainer != null) {
                            previewParams.addRule(RelativeLayout.LEFT_OF, controlsContainer.getId());
                        }

                        previewParams.setMargins(0, 0, 0, 0);
                        previewView.setLayoutParams(previewParams);

                        // Force update the scale type
                        previewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);

                        // Add a second layout listener for fine-tuning after the initial layout
                        previewView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                // Remove this listener after execution
                                previewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                if (previewView != null && isLandscape) {
                                    previewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentActivity fragmentActivity = requireActivity();
        displayMetrics = fragmentActivity.getResources().getDisplayMetrics();
        int margin = (int) (20 * displayMetrics.density);
        int barHeight = (int) (100 * displayMetrics.density);

        // Check if device is in landscape mode
        isLandscape = isLandscapeMode(fragmentActivity);

        relativeLayout = new RelativeLayout(fragmentActivity);

        // Create a black background that fills the entire screen
        View blackBackground = new View(fragmentActivity);
        blackBackground.setId(View.generateViewId());
        blackBackground.setBackgroundColor(Color.BLACK);
        RelativeLayout.LayoutParams blackBgParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        );
        blackBackground.setLayoutParams(blackBgParams);
        relativeLayout.addView(blackBackground); // Add the background first

        ColorStateList buttonColors = createButtonColorList();

        // Create the preview view first
        createPreviewView(fragmentActivity);

        createFocusIndicator(fragmentActivity);

        if (isLandscape) {
            // In landscape mode, create a container for controls on the right side
            createControlsContainerForLandscape(fragmentActivity, buttonColors, margin);
        } else {
            // In portrait mode, create the bottom bar and buttons
            createBottomBar(fragmentActivity, barHeight, margin, buttonColors);

            // Set preview view to be above bottom bar in portrait mode
            RelativeLayout.LayoutParams previewParams = (RelativeLayout.LayoutParams) previewView.getLayoutParams();
            previewParams.addRule(RelativeLayout.ABOVE, bottomBar.getId());
            previewView.setLayoutParams(previewParams);

            // Zoom bar is above the bottom bar/buttons
            createZoomTabLayout(fragmentActivity, margin);

            // Thumbnail images in the filmstrip are above the zoom buttons
            createFilmstripView(fragmentActivity);

            // Close button and flash are top left/right corners
            createCloseButton(fragmentActivity, margin, buttonColors);
            createFlashButton(fragmentActivity, margin, buttonColors);
        }

        // Set a transparent navigation bar
        Window window = requireActivity().getWindow();
        window.setStatusBarColor(Color.BLACK);
        window.setNavigationBarColor(Color.BLACK);

        // Enable immersive fullscreen mode (hide status and navigation bars)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.hide(android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            View decorView = window.getDecorView();
            int flags = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(flags);
        }

        // Remove edge-to-edge insets handling for true fullscreen
        requireActivity().getWindow().setDecorFitsSystemWindows(true);

        return relativeLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Request focus for the root view to ensure it can receive touch events immediately.
        view.setFocusableInTouchMode(true);
        view.requestFocus();

        cameraController = new LifecycleCameraController(requireActivity());
        cameraController.bindToLifecycle(requireActivity());
        previewView.setController(cameraController);
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Use ViewTreeObserver to ensure layout is complete before setting up camera
        previewView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to prevent multiple callbacks
                previewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setupCamera();
            }
        });
    }

    /**
     * Safely adds an image to the cache
     *
     * @param uri The URI of the image
     * @param bitmap The bitmap to cache
     */
    private void addImageToCache(Uri uri, Bitmap bitmap) {
        if (uri != null && bitmap != null && !bitmap.isRecycled() && imageCache != null) {
            imageCache.put(uri, bitmap);
        }
    }

    /**
     * Safely retrieves an image from the cache
     *
     * @param uri The URI of the image to retrieve
     * @return The cached bitmap, or null if not found
     */
    private Bitmap getImageFromCache(Uri uri) {
        return imageCache != null ? imageCache.get(uri) : null;
    }

    /**
     * Gets all image URIs currently in the cache
     *
     * @return A HashMap of all cached images
     */
    private HashMap<Uri, Bitmap> getAllCachedImages() {
        HashMap<Uri, Bitmap> result = new HashMap<>();
        if (imageCache != null) {
            // We need to manually iterate through the snapshot to get all entries
            for (Map.Entry<Uri, Bitmap> entry : imageCache.snapshot().entrySet()) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private void cancel() {
        // When the user cancels the camera session, it should clean up all the photos that were
        // taken.
        int failedDeletions = 0;
        for (Map.Entry<Uri, Bitmap> image : getAllCachedImages().entrySet()) {
            if (!deleteFile(image.getKey())) {
                failedDeletions++;
                Log.w(TAG, "Failed to delete image during cancel: " + image.getKey());
            }
        }

        if (failedDeletions > 0) {
            Log.w(TAG, "Failed to delete " + failedDeletions + " images during cancel");
            // We still proceed with cancellation even if some deletions failed
        }

        if (imagesCapturedCallback != null) {
            imagesCapturedCallback.onCaptureCanceled();
        }
        closeFragment();
    }

    private void done() {
        if (imagesCapturedCallback != null) {
            imagesCapturedCallback.onCaptureSuccess(getAllCachedImages());
        }
        closeFragment();
    }

    /**
     * Safely closes the fragment, handling any potential exceptions
     */
    private void closeFragment() {
        try {
            if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
                requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            } else {
                Log.w(TAG, "Cannot close fragment: activity is null, finishing, or fragment not added");
            }
        } catch (IllegalStateException e) {
            // This can happen if the activity is being destroyed
            Log.e(TAG, "Error closing fragment", e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error closing fragment", e);
        }
    }

    public void setImagesCapturedCallback(OnImagesCapturedCallback imagesCapturedCallback) {
        this.imagesCapturedCallback = imagesCapturedCallback;
    }

    private void createBottomBar(FragmentActivity fragmentActivity, int barHeight, int margin, ColorStateList buttonColors) {
        bottomBar = new RelativeLayout(fragmentActivity);
        bottomBar.setId(View.generateViewId());
        bottomBar.setBackgroundColor(Color.BLACK);
        bottomBarLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, barHeight);
        bottomBarLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottomBar.setLayoutParams(bottomBarLayoutParams);
        relativeLayout.addView(bottomBar);

        createTakePictureButton(fragmentActivity, margin, buttonColors);
        createFlipButton(fragmentActivity, margin, buttonColors);
        createDoneButton(fragmentActivity, margin, buttonColors);
    }

    private void createTakePictureButtonForLandscape(FragmentActivity fragmentActivity, int margin, ColorStateList buttonColors) {
        takePictureButton = new FloatingActionButton(fragmentActivity);
        takePictureButton.setId(View.generateViewId());
        takePictureButton.setImageResource(R.drawable.ic_shutter_circle);
        takePictureButton.setBackgroundColor(Color.TRANSPARENT);
        takePictureButton.setBackgroundTintList(buttonColors);

        int fabSize = dpToPx(fragmentActivity, 84);
        int iconSize = (int) (fabSize * 0.9);
        takePictureButton.setCustomSize(fabSize);
        takePictureButton.setMaxImageSize(iconSize);

        takePictureLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        // Position in the center of the right side
        takePictureLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        takePictureLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        takePictureButton.setLayoutParams(takePictureLayoutParams);
        takePictureButton.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(fragmentActivity, R.animator.button_press_animation));
        takePictureButton.setOnClickListener(
            v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                mediaActionSound.play(MediaActionSound.SHUTTER_CLICK);

                // Add loading thumbnail immediately for visual feedback
                if (thumbnailAdapter != null) {
                    thumbnailAdapter.addLoadingThumbnail();
                    // Scroll to show the new loading thumbnail
                    if (filmstripView != null) {
                        filmstripView.scrollToPosition(thumbnailAdapter.getItemCount() - 1);
                    }
                }

                var name = new SimpleDateFormat(FILENAME, Locale.US).format(System.currentTimeMillis());
                var contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, PHOTO_TYPE);
                var outputOptions = new ImageCapture.OutputFileOptions.Builder(
                    requireContext().getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                    .build();

                cameraController.takePicture(
                    outputOptions,
                    cameraExecutor,
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            Uri savedImageUri = outputFileResults.getSavedUri();
                            if (savedImageUri != null) {
                                InputStream stream = null;
                                try {
                                    stream = requireContext().getContentResolver().openInputStream(savedImageUri);
                                    if (stream == null) {
                                        Log.e(TAG, "Failed to open input stream for saved image: " + savedImageUri);
                                        showErrorToast("Failed to process captured image");
                                        return;
                                    }

                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                    Bitmap bmp = BitmapFactory.decodeStream(stream, null, options);

                                    if (bmp == null) {
                                        Log.e(TAG, "Failed to decode bitmap from saved image: " + savedImageUri);
                                        showErrorToast("Failed to process captured image");
                                        return;
                                    }

                                    addImageToCache(savedImageUri, bmp);

                                    // Generate thumbnail on a background thread to avoid UI jank
                                    if (cameraExecutor != null && !cameraExecutor.isShutdown()) {
                                        cameraExecutor.execute(() -> {
                                            final Bitmap thumbnail = getThumbnail(savedImageUri);
                                            // Update UI on main thread
                                            requireActivity().runOnUiThread(() -> {
                                                if (thumbnailAdapter != null) {
                                                    thumbnailAdapter.replaceLoadingThumbnail(savedImageUri, thumbnail);
                                                }
                                            });
                                        });
                                    }
                                } catch (FileNotFoundException e) {
                                    Log.e(TAG, "File not found for saved image: " + savedImageUri, e);
                                    showErrorToast("Image file not found");
                                } catch (OutOfMemoryError e) {
                                    Log.e(TAG, "Out of memory when processing image: " + savedImageUri, e);
                                    showErrorToast("Not enough memory to process image");
                                    // Try to recover by clearing the cache
                                    if (imageCache != null) {
                                        imageCache.evictAll();
                                    }
                                    System.gc(); // Request garbage collection
                                } catch (Exception e) {
                                    Log.e(TAG, "Error processing saved image: " + savedImageUri, e);
                                    showErrorToast("Error processing image");
                                } finally {
                                    if (stream != null) {
                                        try {
                                            stream.close();
                                        } catch (IOException e) {
                                            Log.e(TAG, "Error closing input stream", e);
                                        }
                                    }
                                }
                            } else {
                                Log.e(TAG, "Saved image URI is null");
                                showErrorToast("Failed to save image");
                            }
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            int errorCode = exception.getImageCaptureError();
                            String errorMessage;

                            switch (errorCode) {
                                case ImageCapture.ERROR_CAMERA_CLOSED:
                                    errorMessage = "Camera was closed during capture";
                                    break;
                                case ImageCapture.ERROR_CAPTURE_FAILED:
                                    errorMessage = "Image capture failed";
                                    break;
                                case ImageCapture.ERROR_FILE_IO:
                                    errorMessage = "File write operation failed";
                                    break;
                                case ImageCapture.ERROR_INVALID_CAMERA:
                                    errorMessage = "Selected camera cannot be found";
                                    break;
                                default:
                                    errorMessage = "Unknown error during image capture";
                                    break;
                            }

                            Log.e(TAG, "Image capture error: " + errorMessage, exception);

                            // Remove any loading thumbnails since capture failed
                            requireActivity().runOnUiThread(() -> {
                                if (thumbnailAdapter != null && thumbnailAdapter.hasLoadingThumbnails()) {
                                    thumbnailAdapter.removeLoadingThumbnails();
                                }
                            });

                            showErrorToast(errorMessage);
                        }
                    }
                );
            }
        );
        controlsContainer.addView(takePictureButton);
    }

    private void createFlipButtonForLandscape(FragmentActivity fragmentActivity, int margin, ColorStateList buttonColors) {
        flipCameraButton = new FloatingActionButton(fragmentActivity);
        flipCameraButton.setId(View.generateViewId());
        flipCameraButton.setImageResource(R.drawable.flip_camera_android_24px);
        flipCameraButton.setColorFilter(Color.WHITE);
        flipCameraButton.setBackgroundTintList(buttonColors);
        flipButtonLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        // Position at the bottom right
        flipButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        flipButtonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        flipButtonLayoutParams.setMargins(0, 0, 0, margin * 2);
        flipCameraButton.setLayoutParams(flipButtonLayoutParams);
        flipCameraButton.setOnClickListener(
            v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                // Clean up any loading thumbnails since camera swap will cancel ongoing captures
                if (thumbnailAdapter != null && thumbnailAdapter.hasLoadingThumbnails()) {
                    thumbnailAdapter.removeLoadingThumbnails();
                    showErrorToast("Capture cancelled due to camera switch");
                }

                lensFacing = lensFacing == CameraSelector.LENS_FACING_FRONT ? CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_FRONT;
                flashButton.setVisibility(lensFacing == CameraSelector.LENS_FACING_BACK ? View.VISIBLE : View.GONE);
                if (!zoomTabs.isEmpty()) {
                    zoomTabLayout.removeAllTabs();
                    zoomTabs.clear();
                }
                setupCamera();
            }
        );
        controlsContainer.addView(flipCameraButton);
    }

    private void createDoneButtonForLandscape(FragmentActivity fragmentActivity, int margin, ColorStateList buttonColors) {
        doneButton = new FloatingActionButton(fragmentActivity);
        doneButton.setId(View.generateViewId());
        doneButton.setImageResource(R.drawable.done_24px);
        doneButton.setColorFilter(Color.WHITE);
        doneButton.setBackgroundTintList(buttonColors);
        doneButtonLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        // Position at the top right
        doneButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        doneButtonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        doneButtonLayoutParams.setMargins(0, margin * 2, 0, 0);
        doneButton.setLayoutParams(doneButtonLayoutParams);
        doneButton.setOnClickListener(
            view -> {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                done();
            }
        );
        controlsContainer.addView(doneButton);
    }

    private void createCloseButtonForLandscape(FragmentActivity fragmentActivity, int margin, ColorStateList buttonColors) {
        closeButton = new FloatingActionButton(fragmentActivity);
        closeButton.setId(View.generateViewId());
        closeButton.setImageResource(R.drawable.close_24px);
        closeButton.setBackgroundTintList(buttonColors);
        closeButton.setColorFilter(Color.WHITE);
        closeButtonLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        // Position at the top left of the preview area
        closeButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        closeButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        closeButtonLayoutParams.setMargins(margin * 2, margin * 2, 0, 0);
        closeButton.setLayoutParams(closeButtonLayoutParams);
        closeButton.setOnClickListener(
            view -> {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if (imageCache != null && imageCache.size() > 0) {
                    new AlertDialog.Builder(requireContext())
                    .setMessage(CONFIRM_CANCEL_MESSAGE)
                    .setPositiveButton(CONFIRM_CANCEL_POSITIVE, (dialogInterface, i) -> cancel())
                    .setNegativeButton(CONFIRM_CANCEL_NEGATIVE, (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();
                } else {
                    cancel();
                }
            }
        );

        // Add to the main layout instead of the controls container
        relativeLayout.addView(closeButton);
    }

    private void createFlashButtonForLandscape(FragmentActivity fragmentActivity, int margin, ColorStateList buttonColors) {
        flashButton = new FloatingActionButton(fragmentActivity);
        flashButton.setId(View.generateViewId());
        flashButton.setImageResource(R.drawable.flash_auto_24px);
        flashButton.setBackgroundTintList(buttonColors);
        flashButton.setColorFilter(Color.WHITE);
        flashButtonLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        // Position at the bottom left of the preview area
        flashButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        flashButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        flashButtonLayoutParams.setMargins(margin * 2, 0, 0, margin * 2);
        flashButton.setLayoutParams(flashButtonLayoutParams);
        flashButton.setOnClickListener(
            view -> {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                flashMode = cameraController.getImageCaptureFlashMode();
                switch (flashMode) {
                    case ImageCapture.FLASH_MODE_OFF:
                        {
                            flashMode = ImageCapture.FLASH_MODE_ON;
                            flashButton.setImageResource(R.drawable.flash_on_24px);
                            flashButton.setColorFilter(Color.WHITE);
                            break;
                        }
                    case ImageCapture.FLASH_MODE_ON:
                        {
                            flashMode = ImageCapture.FLASH_MODE_AUTO;
                            flashButton.setImageResource(R.drawable.flash_auto_24px);
                            flashButton.setColorFilter(Color.WHITE);
                            break;
                        }
                    case ImageCapture.FLASH_MODE_AUTO:
                        {
                            flashMode = ImageCapture.FLASH_MODE_OFF;
                            flashButton.setImageResource(R.drawable.flash_off_24px);
                            flashButton.setColorFilter(Color.WHITE);
                            break;
                        }
                    default:
                        throw new IllegalStateException("Unexpected flash mode: " + flashMode);
                }
                cameraController.setImageCaptureFlashMode(flashMode);
            }
        );

        // Add to the main layout instead of the controls container
        relativeLayout.addView(flashButton);
    }

    private void createFlashButton(FragmentActivity fragmentActivity, int margin, ColorStateList buttonColors) {
        flashButton = new FloatingActionButton(fragmentActivity);
        flashButton.setId(View.generateViewId());
        flashButton.setImageResource(R.drawable.flash_auto_24px);
        flashButton.setBackgroundTintList(buttonColors);
        flashButton.setColorFilter(Color.WHITE);
        flashButtonLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        flashButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        flashButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        int topMargin = (int) (margin * 2.5);
        flashButtonLayoutParams.setMargins(0, topMargin, margin, 0);
        flashButton.setLayoutParams(flashButtonLayoutParams);
        flashButton.setOnClickListener(
            view -> {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                flashMode = cameraController.getImageCaptureFlashMode();
                switch (flashMode) {
                    case ImageCapture.FLASH_MODE_OFF:
                        {
                            flashMode = ImageCapture.FLASH_MODE_ON;
                            flashButton.setImageResource(R.drawable.flash_on_24px);
                            flashButton.setColorFilter(Color.WHITE);
                            break;
                        }
                    case ImageCapture.FLASH_MODE_ON:
                        {
                            flashMode = ImageCapture.FLASH_MODE_AUTO;
                            flashButton.setImageResource(R.drawable.flash_auto_24px);
                            flashButton.setColorFilter(Color.WHITE);
                            break;
                        }
                    case ImageCapture.FLASH_MODE_AUTO:
                        {
                            flashMode = ImageCapture.FLASH_MODE_OFF;
                            flashButton.setImageResource(R.drawable.flash_off_24px);
                            flashButton.setColorFilter(Color.WHITE);
                            break;
                        }
                    default:
                        throw new IllegalStateException("Unexpected flash mode: " + flashMode);
                }
                cameraController.setImageCaptureFlashMode(flashMode);
            }
        );
        relativeLayout.addView(flashButton);
    }

    private void createTakePictureButton(FragmentActivity fragmentActivity, int margin, ColorStateList buttonColors) {
        takePictureButton = new FloatingActionButton(fragmentActivity);
        takePictureButton.setId(View.generateViewId());
        takePictureButton.setImageResource(R.drawable.ic_shutter_circle);
        takePictureButton.setBackgroundColor(Color.TRANSPARENT);
        takePictureButton.setBackgroundTintList(buttonColors);

        int fabSize = dpToPx(fragmentActivity, 84);
        int iconSize = (int) (fabSize * 0.9);
        takePictureButton.setCustomSize(fabSize);
        takePictureButton.setMaxImageSize(iconSize);

        takePictureLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        takePictureLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        takePictureButton.setLayoutParams(takePictureLayoutParams);
        takePictureButton.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(fragmentActivity, R.animator.button_press_animation));
        takePictureButton.setOnClickListener(
            v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                mediaActionSound.play(MediaActionSound.SHUTTER_CLICK);

                // Add loading thumbnail immediately for visual feedback
                if (thumbnailAdapter != null) {
                    thumbnailAdapter.addLoadingThumbnail();
                    // Scroll to show the new loading thumbnail
                    if (filmstripView != null) {
                        filmstripView.scrollToPosition(thumbnailAdapter.getItemCount() - 1);
                    }
                }

                var name = new SimpleDateFormat(FILENAME, Locale.US).format(System.currentTimeMillis());
                var contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, PHOTO_TYPE);
                var outputOptions = new ImageCapture.OutputFileOptions.Builder(
                    requireContext().getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                    .build();

                cameraController.takePicture(
                    outputOptions,
                    cameraExecutor,
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            Uri savedImageUri = outputFileResults.getSavedUri();
                            if (savedImageUri != null) {
                                InputStream stream = null;
                                try {
                                    stream = requireContext().getContentResolver().openInputStream(savedImageUri);
                                    if (stream == null) {
                                        Log.e(TAG, "Failed to open input stream for saved image: " + savedImageUri);
                                        showErrorToast("Failed to process captured image");
                                        return;
                                    }

                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                    Bitmap bmp = BitmapFactory.decodeStream(stream, null, options);

                                    if (bmp == null) {
                                        Log.e(TAG, "Failed to decode bitmap from saved image: " + savedImageUri);
                                        showErrorToast("Failed to process captured image");
                                        return;
                                    }

                                    addImageToCache(savedImageUri, bmp);

                                    // Generate thumbnail on a background thread to avoid UI jank
                                    if (cameraExecutor != null && !cameraExecutor.isShutdown()) {
                                        cameraExecutor.execute(() -> {
                                            final Bitmap thumbnail = getThumbnail(savedImageUri);
                                            // Update UI on main thread
                                            requireActivity().runOnUiThread(() -> {
                                                if (thumbnailAdapter != null) {
                                                    thumbnailAdapter.replaceLoadingThumbnail(savedImageUri, thumbnail);
                                                }
                                            });
                                        });
                                    }
                                } catch (FileNotFoundException e) {
                                    Log.e(TAG, "File not found for saved image: " + savedImageUri, e);
                                    showErrorToast("Image file not found");
                                } catch (OutOfMemoryError e) {
                                    Log.e(TAG, "Out of memory when processing image: " + savedImageUri, e);
                                    showErrorToast("Not enough memory to process image");
                                    // Try to recover by clearing the cache
                                    if (imageCache != null) {
                                        imageCache.evictAll();
                                    }
                                    System.gc(); // Request garbage collection
                                } catch (Exception e) {
                                    Log.e(TAG, "Error processing saved image: " + savedImageUri, e);
                                    showErrorToast("Error processing image");
                                } finally {
                                    if (stream != null) {
                                        try {
                                            stream.close();
                                        } catch (IOException e) {
                                            Log.e(TAG, "Error closing input stream", e);
                                        }
                                    }
                                }
                            } else {
                                Log.e(TAG, "Saved image URI is null");
                                showErrorToast("Failed to save image");
                            }
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            int errorCode = exception.getImageCaptureError();
                            String errorMessage;

                            switch (errorCode) {
                                case ImageCapture.ERROR_CAMERA_CLOSED:
                                    errorMessage = "Camera was closed during capture";
                                    break;
                                case ImageCapture.ERROR_CAPTURE_FAILED:
                                    errorMessage = "Image capture failed";
                                    break;
                                case ImageCapture.ERROR_FILE_IO:
                                    errorMessage = "File write operation failed";
                                    break;
                                case ImageCapture.ERROR_INVALID_CAMERA:
                                    errorMessage = "Selected camera cannot be found";
                                    break;
                                default:
                                    errorMessage = "Unknown error during image capture";
                                    break;
                            }

                            Log.e(TAG, "Image capture error: " + errorMessage, exception);

                            // Remove any loading thumbnails since capture failed
                            requireActivity().runOnUiThread(() -> {
                                if (thumbnailAdapter != null && thumbnailAdapter.hasLoadingThumbnails()) {
                                    thumbnailAdapter.removeLoadingThumbnails();
                                }
                            });

                            showErrorToast(errorMessage);
                        }
                    }
                );
            }
        );
        bottomBar.addView(takePictureButton);
    }

    private void createFlipButton(FragmentActivity fragmentActivity, int margin, ColorStateList buttonColors) {
        flipCameraButton = new FloatingActionButton(fragmentActivity);
        flipCameraButton.setId(View.generateViewId());
        flipCameraButton.setImageResource(R.drawable.flip_camera_android_24px);
        flipCameraButton.setColorFilter(Color.WHITE);
        flipCameraButton.setBackgroundTintList(buttonColors);
        flipButtonLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        flipButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        flipButtonLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        flipButtonLayoutParams.setMargins(margin, 0, 0, 0);
        flipCameraButton.setLayoutParams(flipButtonLayoutParams);
        flipCameraButton.setOnClickListener(
            v -> {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                // Clean up any loading thumbnails since camera swap will cancel ongoing captures
                if (thumbnailAdapter != null && thumbnailAdapter.hasLoadingThumbnails()) {
                    thumbnailAdapter.removeLoadingThumbnails();
                    showErrorToast("Capture cancelled due to camera switch");
                }

                lensFacing = lensFacing == CameraSelector.LENS_FACING_FRONT ? CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_FRONT;
                flashButton.setVisibility(lensFacing == CameraSelector.LENS_FACING_BACK ? View.VISIBLE : View.GONE);
                if (!zoomTabs.isEmpty()) {
                    zoomTabLayout.removeAllTabs();
                    zoomTabs.clear();
                }
                setupCamera();
            }
        );
        bottomBar.addView(flipCameraButton);
    }

    /**
     * Creates a container for camera controls in landscape mode
     */
    private void createControlsContainerForLandscape(FragmentActivity fragmentActivity, ColorStateList buttonColors, int margin) {
        // Create a container for controls on the right side
        controlsContainer = new RelativeLayout(fragmentActivity);
        controlsContainer.setId(View.generateViewId());
        controlsContainer.setBackgroundColor(Color.BLACK);

        // Set the container to take up the right 20% of the screen
        int containerWidth = (int) (displayMetrics.widthPixels * 0.2);
        RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(
            containerWidth,
            RelativeLayout.LayoutParams.MATCH_PARENT
        );
        containerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        controlsContainer.setLayoutParams(containerParams);

        // Add the container to the main layout
        relativeLayout.addView(controlsContainer);

        // First remove any existing layout rules from the preview view
        RelativeLayout.LayoutParams previewParams = (RelativeLayout.LayoutParams) previewView.getLayoutParams();
        previewParams.width = displayMetrics.widthPixels - containerWidth;
        previewParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;

        // Clear any existing rules that might be interfering
        previewParams.removeRule(RelativeLayout.ABOVE);
        previewParams.removeRule(RelativeLayout.BELOW);
        previewParams.removeRule(RelativeLayout.RIGHT_OF);
        previewParams.removeRule(RelativeLayout.LEFT_OF);
        previewParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
        previewParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
        previewParams.removeRule(RelativeLayout.CENTER_VERTICAL);

        // Set the correct rules for landscape mode
        previewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        previewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        previewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        previewParams.addRule(RelativeLayout.LEFT_OF, controlsContainer.getId());

        // Remove any margins
        previewParams.setMargins(0, 0, 0, 0);
        previewView.setLayoutParams(previewParams);

        // Force the scale type to FILL_CENTER to ensure it fills the available space
        previewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);

        // Ensure the preview view has a black background to prevent transparency
        previewView.setBackgroundColor(Color.BLACK);

        // Create camera control buttons for landscape mode that go in the right container
        createTakePictureButtonForLandscape(fragmentActivity, margin, buttonColors);
        createFlipButtonForLandscape(fragmentActivity, margin, buttonColors);
        createDoneButtonForLandscape(fragmentActivity, margin, buttonColors);

        // Create buttons that go directly on the main layout (left side)
        createCloseButtonForLandscape(fragmentActivity, margin, buttonColors);
        createFlashButtonForLandscape(fragmentActivity, margin, buttonColors);

        // Create zoom tabs for landscape mode
        createZoomTabLayoutForLandscape(fragmentActivity, margin);

        // Create filmstrip for landscape mode
        createFilmstripViewForLandscape(fragmentActivity);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createPreviewView(FragmentActivity fragmentActivity) {
        previewView = new PreviewView(fragmentActivity);
        previewView.setId(View.generateViewId());

        RelativeLayout.LayoutParams previewLayoutParams;

        if (isLandscape) {
            // In landscape mode, explicitly set width to account for controls container
            int containerWidth = (int) (displayMetrics.widthPixels * 0.2);
            previewLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            );
            // Initially set to match_parent, we'll adjust the width after the controls container is created
            previewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            previewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            previewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            previewLayoutParams.setMargins(0, 0, 0, 0);
        } else {
            // In portrait mode, use match_parent for width
            previewLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            );
            // We'll set the ABOVE rule after bottomBar is created
        }

        previewView.setLayoutParams(previewLayoutParams);

        // Set background color to black to prevent transparency
        previewView.setBackgroundColor(Color.BLACK);

        // Use FILL_CENTER for both orientations to ensure the preview fills the available space
        previewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);

        previewView.setOnTouchListener(
            (v, event) -> {
                if (focusIndicator != null) {
                    // Position the focus indicator at the touch point
                    focusIndicator.setX(event.getX() - (focusIndicator.getWidth() / 2f));
                    focusIndicator.setY(event.getY() - (focusIndicator.getHeight() / 2f));
                }

                // Let the PreviewView handle the rest of the touch event.
                // Returning false allows the default tap-to-focus behavior to trigger.
                return false;
            }
        );

        relativeLayout.addView(previewView);
    }

    private void createFocusIndicator(Context context) {
        focusIndicator = new ImageView(context);
        focusIndicator.setImageResource(R.drawable.center_focus_24px);

        int size = dpToPx(context, 72);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(size, size);
        focusIndicator.setLayoutParams(layoutParams);

        focusIndicator.setColorFilter(Color.WHITE);
        focusIndicator.setVisibility(View.INVISIBLE); // Initially hidden

        relativeLayout.addView(focusIndicator);
    }

    private void createZoomTabLayout(FragmentActivity fragmentActivity, int margin) {
        zoomTabCardView = new CardView(fragmentActivity);
        zoomTabCardView.setId(View.generateViewId());

        // Make the card view rounded corners
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setShape(GradientDrawable.RECTANGLE);
        backgroundDrawable.setColor(ZOOM_TAB_LAYOUT_BACKGROUND_COLOR);
        backgroundDrawable.setCornerRadius(dpToPx(requireContext(), 56 / 2));
        zoomTabCardView.setBackground(backgroundDrawable);

        // Define the LayoutParams for the cardView
        cardViewLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        cardViewLayoutParams.addRule(RelativeLayout.ABOVE, bottomBar.getId());
        cardViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        cardViewLayoutParams.setMargins(margin, margin, margin, margin); // Adjust bottom margin as needed
        zoomTabCardView.setLayoutParams(cardViewLayoutParams);

        relativeLayout.addView(zoomTabCardView);

        zoomTabLayout = new TabLayout(fragmentActivity);
        zoomTabLayout.setId(View.generateViewId());
        tabLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        zoomTabLayout.setLayoutParams(tabLayoutParams);

        // Set TabLayout parameters
        zoomTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        zoomTabLayout.setTabMode(TabLayout.MODE_FIXED);
        zoomTabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT); // No indicator color
        zoomTabLayout.setSelectedTabIndicator(null);
        zoomTabLayout.setBackgroundColor(Color.TRANSPARENT); // Transparent background to let card view color show
        zoomTabLayout.setBackground(null);

        // Set the listener for tab selection to change the text color and background
        zoomTabLayout.addOnTabSelectedListener(
            new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    ZoomTab zoomTab = zoomTabs.get(tab.getPosition());
                    zoomTab.setSelected(true);
                    if (!isSnappingZoom.get()) {
                        zoomTab.setTransientZoomLevel(null);
                        if (cameraController != null) {
                            cameraController.setZoomRatio(zoomTab.getZoomLevel());
                        }
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    ZoomTab zoomTab = zoomTabs.get(tab.getPosition());
                    zoomTab.setSelected(false);
                    zoomTab.setTransientZoomLevel(null);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    ZoomTab zoomTab = zoomTabs.get(tab.getPosition());
                    zoomTab.setSelected(true);
                    if (!isSnappingZoom.get()) {
                        zoomTab.setTransientZoomLevel(null);
                        if (cameraController != null) {
                            cameraController.setZoomRatio(zoomTab.getZoomLevel());
                        }
                    }
                }
            }
        );

        zoomTabCardView.addView(zoomTabLayout);

        // Use ViewTreeObserver to ensure zoom tab layout is properly laid out
        zoomTabCardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to prevent multiple callbacks
                zoomTabCardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Ensure the tab layout has the correct width
                if (zoomTabLayout.getWidth() > 0) {
                    // Distribute tab width evenly
                    TabLayout.Tab tab = zoomTabLayout.getTabAt(0);
                    if (tab != null && tab.view != null) {
                        int tabWidth = zoomTabLayout.getWidth() / zoomTabLayout.getTabCount();
                        for (int i = 0; i < zoomTabLayout.getTabCount(); i++) {
                            TabLayout.Tab currentTab = zoomTabLayout.getTabAt(i);
                            if (currentTab != null && currentTab.view != null) {
                                ViewGroup.LayoutParams layoutParams = currentTab.view.getLayoutParams();
                                layoutParams.width = tabWidth;
                                currentTab.view.setLayoutParams(layoutParams);
                            }
                        }
                    }
                }
            }
        });
    }

    private void createZoomTabLayoutForLandscape(FragmentActivity fragmentActivity, int margin) {
        zoomTabCardView = new CardView(fragmentActivity);
        zoomTabCardView.setId(View.generateViewId());

        // Make the card view rounded corners
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setShape(GradientDrawable.RECTANGLE);
        backgroundDrawable.setColor(ZOOM_TAB_LAYOUT_BACKGROUND_COLOR);
        backgroundDrawable.setCornerRadius(dpToPx(requireContext(), 56 / 2));
        zoomTabCardView.setBackground(backgroundDrawable);

        // Define the LayoutParams for the cardView in landscape mode
        cardViewLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        // Position it below the flip button
        cardViewLayoutParams.addRule(RelativeLayout.BELOW, flipCameraButton.getId());
        cardViewLayoutParams.addRule(RelativeLayout.ABOVE, takePictureButton.getId());
        cardViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        cardViewLayoutParams.setMargins(margin, margin, margin, margin);
        zoomTabCardView.setLayoutParams(cardViewLayoutParams);

        controlsContainer.addView(zoomTabCardView);

        zoomTabLayout = new TabLayout(fragmentActivity);
        zoomTabLayout.setId(View.generateViewId());
        tabLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        zoomTabLayout.setLayoutParams(tabLayoutParams);

        // Set TabLayout parameters
        zoomTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        zoomTabLayout.setTabMode(TabLayout.MODE_FIXED);
        zoomTabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        zoomTabLayout.setSelectedTabIndicator(null);
        zoomTabLayout.setBackgroundColor(Color.TRANSPARENT);
        zoomTabLayout.setBackground(null);

        // Set the listener for tab selection
        zoomTabLayout.addOnTabSelectedListener(
            new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    ZoomTab zoomTab = zoomTabs.get(tab.getPosition());
                    zoomTab.setSelected(true);
                    if (!isSnappingZoom.get()) {
                        zoomTab.setTransientZoomLevel(null);
                        if (cameraController != null) {
                            cameraController.setZoomRatio(zoomTab.getZoomLevel());
                        }
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    ZoomTab zoomTab = zoomTabs.get(tab.getPosition());
                    zoomTab.setSelected(false);
                    zoomTab.setTransientZoomLevel(null);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    ZoomTab zoomTab = zoomTabs.get(tab.getPosition());
                    zoomTab.setSelected(true);
                    if (!isSnappingZoom.get()) {
                        zoomTab.setTransientZoomLevel(null);
                        if (cameraController != null) {
                            cameraController.setZoomRatio(zoomTab.getZoomLevel());
                        }
                    }
                }
            }
        );

        zoomTabCardView.addView(zoomTabLayout);

        // Use ViewTreeObserver to ensure zoom tab layout is properly laid out in landscape mode
        zoomTabCardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to prevent multiple callbacks
                zoomTabCardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Ensure the tab layout has the correct width
                if (zoomTabLayout.getWidth() > 0) {
                    // Distribute tab width evenly
                    TabLayout.Tab tab = zoomTabLayout.getTabAt(0);
                    if (tab != null && tab.view != null) {
                        int tabWidth = zoomTabLayout.getWidth() / zoomTabLayout.getTabCount();
                        for (int i = 0; i < zoomTabLayout.getTabCount(); i++) {
                            TabLayout.Tab currentTab = zoomTabLayout.getTabAt(i);
                            if (currentTab != null && currentTab.view != null) {
                                ViewGroup.LayoutParams layoutParams = currentTab.view.getLayoutParams();
                                layoutParams.width = tabWidth;
                                currentTab.view.setLayoutParams(layoutParams);
                            }
                        }
                    }
                }
            }
        });
    }

    private void createZoomTabs(FragmentActivity fragmentActivity, TabLayout tabLayout) {
        float[] zoomLevels = { minZoom, 1f, 2f, 5f };

        for (int i = 0; i < zoomLevels.length; i++) {
            float zoomLevel = zoomLevels[i];
            ZoomTab zoomTab = new ZoomTab(fragmentActivity, zoomLevel, 40, i);
            zoomTabs.add(zoomTab);
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setCustomView(zoomTab.getView());
            tabLayout.addTab(tab);
        }

        tabLayout.selectTab(tabLayout.getTabAt(1));
    }

    private void createFilmstripView(FragmentActivity fragmentActivity) {
        filmstripView = new RecyclerView(fragmentActivity);
        RelativeLayout.LayoutParams filmstripLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        filmstripLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        filmstripLayoutParams.addRule(RelativeLayout.ABOVE, zoomTabCardView.getId());
        filmstripView.setLayoutParams(filmstripLayoutParams);

        // Add padding to the filmstrip to prevent clipping of the remove button
        int padding = dpToPx(fragmentActivity, 12);
        filmstripView.setPadding(padding, padding, padding, padding);
        filmstripView.setClipToPadding(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(fragmentActivity, LinearLayoutManager.HORIZONTAL, false);
        filmstripView.setLayoutManager(layoutManager);

        // If we already have an adapter with thumbnails, preserve them
        ThumbnailAdapter existingAdapter = thumbnailAdapter;
        thumbnailAdapter = new ThumbnailAdapter();

        // If we had an existing adapter with thumbnails, transfer them to the new adapter
        if (existingAdapter != null && existingAdapter.getItemCount() > 0) {
            for (int i = 0; i < existingAdapter.getItemCount(); i++) {
                ThumbnailAdapter.ThumbnailItem item = existingAdapter.getThumbnailItem(i);
                if (item != null) {
                    if (!item.isLoading()) {
                        thumbnailAdapter.addThumbnail(item.getUri(), item.getBitmap());
                    }
                }
            }
        }
        filmstripView.setAdapter(thumbnailAdapter);
        relativeLayout.addView(filmstripView);

        // Use ViewTreeObserver to ensure filmstrip is properly laid out
        filmstripView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to prevent multiple callbacks
                filmstripView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Scroll to the end of the filmstrip to show the most recent thumbnails
                if (thumbnailAdapter.getItemCount() > 0) {
                    filmstripView.scrollToPosition(thumbnailAdapter.getItemCount() - 1);
                }
            }
        });

        thumbnailAdapter.setOnThumbnailsChangedCallback(
            new ThumbnailAdapter.OnThumbnailsChangedCallback() {
                @Override
                public void onThumbnailRemoved(Uri uri, Bitmap bmp) {
                    Bitmap bitmap = getImageFromCache(uri);
                    if (imageCache != null) {
                        imageCache.remove(uri);
                    }

                    if (!deleteFile(uri)) {
                        Log.w(TAG, "Failed to delete file after thumbnail removal: " + uri);
                        // Even if deletion fails, we've already removed it from the UI and cache,
                        // so we don't need to show an error to the user
                    }
                }
            }
        );

        // Set click listener for thumbnails to show preview
        thumbnailAdapter.setOnThumbnailClickListener(new ThumbnailAdapter.OnThumbnailClickListener() {
            @Override
            public void onThumbnailClick(Uri uri, Bitmap bitmap) {
                showImagePreview(uri);
            }
        });
    }

    private void createFilmstripViewForLandscape(FragmentActivity fragmentActivity) {
        filmstripView = new RecyclerView(fragmentActivity);
        RelativeLayout.LayoutParams filmstripLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        // Position the filmstrip at the bottom of the preview area, but to the right of the flash button
        filmstripLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        filmstripLayoutParams.addRule(RelativeLayout.RIGHT_OF, flashButton.getId());

        // Calculate width to fill the remaining space (minus controls container and some margin for the flash button)
        int flashButtonWidth = dpToPx(fragmentActivity, 56); // Approximate width of FAB
        int margin = dpToPx(fragmentActivity, 20);
        // Calculate width to use more of the available space
        // We're keeping the 20% for controls container but reducing other margins
        filmstripLayoutParams.width = displayMetrics.widthPixels -
                                     (int)(displayMetrics.widthPixels * 0.2) - // Controls container
                                     flashButtonWidth - // Flash button width
                                     margin; // Reduced margin to allow more thumbnails

        // Add left margin to create space between flash button and filmstrip
        filmstripLayoutParams.setMargins(margin, 0, 0, margin);

        filmstripView.setLayoutParams(filmstripLayoutParams);

        // Add padding to the filmstrip - reduced for landscape mode
        int padding = dpToPx(fragmentActivity, 6);
        filmstripView.setPadding(padding, padding, padding, padding);
        filmstripView.setClipToPadding(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(fragmentActivity, LinearLayoutManager.HORIZONTAL, false);
        filmstripView.setLayoutManager(layoutManager);

        // Create a custom adapter with smaller thumbnails for landscape mode
        // If we already have an adapter with thumbnails, preserve them
        ThumbnailAdapter existingAdapter = thumbnailAdapter;
        thumbnailAdapter = new ThumbnailAdapter() {
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Context context = parent.getContext();
                int thumbnailSize = dpToPx(context, 60); // Smaller thumbnail size for landscape mode (was 80dp)
                int margin = dpToPx(context, 3); // Smaller margin for landscape mode (was 4dp)

                FrameLayout frameLayout = new FrameLayout(context);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(thumbnailSize, thumbnailSize);
                layoutParams.setMargins(margin, margin, margin, margin);
                frameLayout.setLayoutParams(layoutParams);

                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                frameLayout.addView(imageView);

                ImageView removeButton = new ImageView(context);
                int buttonSize = dpToPx(context, 20); // Smaller remove button (was 24dp)
                FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(buttonSize, buttonSize);
                buttonParams.gravity = Gravity.TOP | Gravity.END;
                removeButton.setLayoutParams(buttonParams);
                removeButton.setImageResource(R.drawable.ic_cancel_white_24dp);
                frameLayout.addView(removeButton);

                // Add progress bar for loading state
                ProgressBar progressBar = new ProgressBar(context);
                int progressSize = dpToPx(context, 24); // Smaller progress bar for landscape mode
                FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(progressSize, progressSize);
                progressParams.gravity = Gravity.CENTER;
                progressBar.setLayoutParams(progressParams);
                progressBar.setVisibility(View.GONE); // Initially hidden
                frameLayout.addView(progressBar);

                return new ViewHolder(frameLayout, imageView, removeButton, progressBar);
            }
        };

        // If we had an existing adapter with thumbnails, transfer them to the new adapter
        if (existingAdapter != null && existingAdapter.getItemCount() > 0) {
            for (int i = 0; i < existingAdapter.getItemCount(); i++) {
                ThumbnailAdapter.ThumbnailItem item = existingAdapter.getThumbnailItem(i);
                if (item != null) {
                    if (!item.isLoading()) {
                        thumbnailAdapter.addThumbnail(item.getUri(), item.getBitmap());
                    }
                }
            }
        }
        filmstripView.setAdapter(thumbnailAdapter);
        relativeLayout.addView(filmstripView);

        // Use ViewTreeObserver to ensure filmstrip is properly laid out in landscape mode
        filmstripView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to prevent multiple callbacks
                filmstripView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Scroll to the end of the filmstrip to show the most recent thumbnails
                if (thumbnailAdapter.getItemCount() > 0) {
                    filmstripView.scrollToPosition(thumbnailAdapter.getItemCount() - 1);
                }
            }
        });

        thumbnailAdapter.setOnThumbnailsChangedCallback(
            new ThumbnailAdapter.OnThumbnailsChangedCallback() {
                @Override
                public void onThumbnailRemoved(Uri uri, Bitmap bmp) {
                    if (imageCache != null) {
                        imageCache.remove(uri);
                    }

                    if (!deleteFile(uri)) {
                        Log.w(TAG, "Failed to delete file after thumbnail removal in landscape mode: " + uri);
                        // Even if deletion fails, we've already removed it from the UI and cache,
                        // so we don't need to show an error to the user
                    }
                }
            }
        );

        // Set click listener for thumbnails to show preview
        thumbnailAdapter.setOnThumbnailClickListener(new ThumbnailAdapter.OnThumbnailClickListener() {
            @Override
            public void onThumbnailClick(Uri uri, Bitmap bitmap) {
                showImagePreview(uri);
            }
        });
    }

    private void createDoneButton(FragmentActivity fragmentActivity, int margin, ColorStateList buttonColors) {
        doneButton = new FloatingActionButton(fragmentActivity);
        doneButton.setId(View.generateViewId());
        doneButton.setImageResource(R.drawable.done_24px);
        doneButton.setColorFilter(Color.WHITE);
        doneButton.setBackgroundTintList(buttonColors);
        doneButtonLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        doneButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        doneButtonLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        doneButtonLayoutParams.setMargins(0, 0, margin, 0);
        doneButton.setLayoutParams(doneButtonLayoutParams);
        doneButton.setOnClickListener(
            view -> {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                done();
            }
        );
        bottomBar.addView(doneButton);
    }

    private void createCloseButton(FragmentActivity fragmentActivity, int margin, ColorStateList buttonColors) {
        closeButton = new FloatingActionButton(fragmentActivity);
        closeButton.setId(View.generateViewId());
        closeButton.setImageResource(R.drawable.close_24px);
        closeButton.setBackgroundTintList(buttonColors);
        closeButton.setColorFilter(Color.WHITE);
        closeButtonLayoutParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        closeButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        closeButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        // Increase top margin for immersive mode (e.g., 2.5x the original margin)
        int topMargin = (int) (margin * 2.5);
        closeButtonLayoutParams.setMargins(margin, topMargin, 0, 0);
        closeButton.setLayoutParams(closeButtonLayoutParams);
        closeButton.setOnClickListener(
            view -> {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if (imageCache != null && imageCache.size() > 0) {
                    new AlertDialog.Builder(requireContext())
                    .setMessage(CONFIRM_CANCEL_MESSAGE)
                    .setPositiveButton(CONFIRM_CANCEL_POSITIVE, (dialogInterface, i) -> cancel())
                    .setNegativeButton(CONFIRM_CANCEL_NEGATIVE, (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();
                } else {
                    cancel();
                }
            }
        );
        relativeLayout.addView(closeButton);
    }

    /**
     * Deletes a file from the device storage
     *
     * @param fileUri The URI of the file to delete
     * @return true if deletion was successful, false otherwise
     */
    private boolean deleteFile(Uri fileUri) {
        if (fileUri == null) {
            Log.e(TAG, "Cannot delete null URI");
            return false;
        }

        try {
            ContentResolver contentResolver = requireContext().getContentResolver();
            int deleted = contentResolver.delete(fileUri, null, null);

            if (deleted == 0) {
                // File deletion failed
                Log.e(TAG, "Failed to delete file: " + fileUri);
                return false;
            } else {
                // File deletion successful
                Log.i(TAG, "File deleted: " + fileUri);
                return true;
            }
        } catch (SecurityException e) {
            // Handle permission issues
            Log.e(TAG, "Security exception when deleting file: " + fileUri, e);
            showErrorToast("Permission denied to delete image");
            return false;
        } catch (IllegalArgumentException e) {
            // Handle invalid URI
            Log.e(TAG, "Invalid URI when deleting file: " + fileUri, e);
            return false;
        } catch (Exception e) {
            // Handle any other exceptions
            Log.e(TAG, "Error deleting file: " + fileUri, e);
            return false;
        }
    }

    /**
     * Shows a full-screen preview of the image when a thumbnail is clicked
     *
     * @param uri The URI of the image to display in the preview
     */
    private void showImagePreview(Uri uri) {
        if (thumbnailAdapter == null) {
            // Fallback to single image preview if no adapter
            ImagePreviewFragment previewFragment = ImagePreviewFragment.newInstance(uri);
            previewFragment.show(requireActivity().getSupportFragmentManager(), "image_preview");
            return;
        }
        
        // Gather all non-loading image URIs and find the current position
        List<Uri> imageUris = new ArrayList<>();
        int currentPosition = 0;
        
        for (int i = 0; i < thumbnailAdapter.getItemCount(); i++) {
            ThumbnailAdapter.ThumbnailItem item = thumbnailAdapter.getThumbnailItem(i);
            if (item != null && !item.isLoading()) {
                if (uri.equals(item.getUri())) {
                    currentPosition = imageUris.size();
                }
                imageUris.add(item.getUri());
            }
        }
        
        if (imageUris.isEmpty()) {
            // Fallback to single image preview if no images found
            ImagePreviewFragment previewFragment = ImagePreviewFragment.newInstance(uri);
            previewFragment.show(requireActivity().getSupportFragmentManager(), "image_preview");
            return;
        }
        
        // Show preview with swipe navigation
        ImagePreviewFragment previewFragment = ImagePreviewFragment.newInstance(imageUris, currentPosition);
        previewFragment.show(requireActivity().getSupportFragmentManager(), "image_preview");
    }

    private void setupCamera() throws IllegalStateException {
        cameraController
            .getInitializationFuture()
            .addListener(() -> {
                if (!hasFrontFacingCamera()) {
                    flipCameraButton.setVisibility(View.GONE);
                }
            }, ContextCompat.getMainExecutor(requireContext()));

        cameraController
            .getZoomState()
            .observe(
                requireActivity(),
                zoomState -> {
                    zoomRatio = zoomState;
                    minZoom = zoomState.getMinZoomRatio();
                    maxZoom = zoomState.getMaxZoomRatio();

                    if (zoomTabs.isEmpty()) {
                        createZoomTabs(requireActivity(), zoomTabLayout);
                    }

                    if (zoomRunnable != null) {
                        zoomHandler.removeCallbacks(zoomRunnable);
                    }

                    zoomRunnable =
                        () -> {
                            float currentZoom = zoomRatio.getZoomRatio();
                            ZoomTab closestTab = null;
                            final float threshold = 0.05f; // Threshold for considering the next zoom level

                            for (int i = 0; i < zoomTabs.size(); i++) {
                                ZoomTab currentTab = zoomTabs.get(i);
                                // Check if this is the last tab or if the current zoom is less than the next tab's level minus the threshold
                                if (i == zoomTabs.size() - 1 || currentZoom < zoomTabs.get(i + 1).zoomLevel - threshold) {
                                    closestTab = currentTab;
                                    break;
                                }
                            }

                            // If we found a closest tab, update its display and select the tab.
                            if (closestTab != null) {
                                TabLayout.Tab tab = zoomTabLayout.getTabAt(closestTab.getTabIndex());
                                if (tab != null) {
                                    closestTab.setTransientZoomLevel(currentZoom); // Update the tab's display to show the current zoom level
                                    isSnappingZoom.set(true);
                                    zoomTabLayout.selectTab(tab); // This will not trigger the camera zoom change due to the isSnappingZoom flag
                                    isSnappingZoom.set(false);
                                }
                            }
                        };
                    zoomHandler.post(zoomRunnable);
                }
            );

        cameraController
            .getTapToFocusState()
            .observe(
                requireActivity(),
                tapToFocusState -> {
                    if (focusIndicator == null) return;
                    // Show and animate the focus indicator when focusing starts
                    if (tapToFocusState == LifecycleCameraController.TAP_TO_FOCUS_STARTED) {
                        focusIndicator.setVisibility(View.VISIBLE);
                        focusIndicator.setAlpha(0f); // Start fully transparent
                        focusIndicator.animate().alpha(1f).setDuration(200).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    } else {
                        // Fade out and hide the focus indicator when focusing ends, regardless of the result
                        focusIndicator
                            .animate()
                            .alpha(0f)
                            .setDuration(500)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .withEndAction(() -> focusIndicator.setVisibility(View.INVISIBLE))
                            .start();
                    }
                }
            );

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
        cameraController.setCameraSelector(cameraSelector);
        cameraController.setPinchToZoomEnabled(true);
        cameraController.setTapToFocusEnabled(true);
        cameraController.setImageCaptureFlashMode(flashMode);
    }

    /**
     * Shows an error toast message to the user
     *
     * @param message The error message to display
     */
    private void showErrorToast(String message) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            requireActivity().runOnUiThread(() -> {
                try {
                    android.widget.Toast.makeText(
                        requireContext(),
                        message,
                        android.widget.Toast.LENGTH_SHORT
                    ).show();
                } catch (Exception e) {
                    // Fail silently if we can't show a toast
                    Log.e(TAG, "Failed to show error toast: " + message, e);
                }
            });
        }
    }

    private boolean hasFrontFacingCamera() {
        if (cameraController != null) {
            CameraSelector frontFacing = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();

            return cameraController.hasCamera(frontFacing);
        }
        return false;
    }

    /**
     * Gets a memory-efficient thumbnail for an image URI using downsampling techniques
     *
     * @param imageUri The URI of the image to create a thumbnail for
     * @return A downsampled bitmap thumbnail or null if creation failed
     */
    @SuppressWarnings("deprecation")
    private Bitmap getThumbnail(Uri imageUri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        InputStream inputStream = null;

        try {
            // Target thumbnail size (smaller than the original implementation to save memory)
            int targetWidth = (int) (displayMetrics.widthPixels * 0.2);
            int targetHeight = (int) (displayMetrics.heightPixels * 0.2);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    // For Android 10+ use the built-in thumbnail loader with our target size
                    Size size = new Size(targetWidth, targetHeight);
                    return contentResolver.loadThumbnail(imageUri, size, null);
                } catch (IOException e) {
                    // Fall back to manual downsampling if the built-in method fails
                    Log.w(TAG, "Failed to load thumbnail with system API, falling back to manual downsampling", e);
                    // Continue to manual downsampling below
                }
            }

            // Manual downsampling approach for pre-Q devices or as fallback

            // FIRST PASS: Decode bounds only to determine dimensions
            BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
            boundsOptions.inJustDecodeBounds = true; // Only decode bounds, not the actual bitmap

            inputStream = contentResolver.openInputStream(imageUri);
            BitmapFactory.decodeStream(inputStream, null, boundsOptions);
            if (inputStream != null) {
                inputStream.close();
            }

            // Calculate optimal inSampleSize for downsampling
            int inSampleSize = calculateInSampleSize(boundsOptions, targetWidth, targetHeight);

            // SECOND PASS: Decode with calculated inSampleSize
            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            decodeOptions.inSampleSize = inSampleSize;
            decodeOptions.inPreferredConfig = Bitmap.Config.RGB_565; // Use RGB_565 instead of ARGB_8888 to reduce memory usage by half

            inputStream = contentResolver.openInputStream(imageUri);
            Bitmap thumbnail = BitmapFactory.decodeStream(inputStream, null, decodeOptions);

            return thumbnail;

        } catch (Exception e) {
            Log.e(TAG, "Error creating thumbnail", e);

            // Last resort fallback for older devices
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                try {
                    String[] projection = { MediaStore.Images.Media._ID };
                    Cursor cursor = contentResolver.query(imageUri, projection, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                        long imageId = cursor.getLong(idColumn);
                        cursor.close();

                        return MediaStore.Images.Thumbnails.getThumbnail(
                            contentResolver,
                            imageId,
                            MediaStore.Images.Thumbnails.MINI_KIND,
                            null
                        );
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Error in thumbnail fallback", ex);
                }
            }

            return null;
        } finally {
            // Ensure streams are always closed
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing input stream", e);
                }
            }
        }
    }

    /**
     * Calculates the optimal inSampleSize value for downsampling
     *
     * @param options BitmapFactory.Options with outWidth and outHeight set
     * @param reqWidth Requested width of the resulting bitmap
     * @param reqHeight Requested height of the resulting bitmap
     * @return The optimal inSampleSize value (power of 2)
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public abstract static class OnImagesCapturedCallback {

        public void onCaptureSuccess(HashMap<Uri, Bitmap> images) {}

        public void onCaptureCanceled() {}
    }

    public class ZoomTab {

        private final float zoomLevel;
        private final int tabIndex;
        private final int circleSize;
        private final TextView textView;
        private final GradientDrawable background;
        private Float transientZoomLevel;

        public ZoomTab(Context context, float zoomLevel, int circleSize, int tabIndex) {
            this.zoomLevel = zoomLevel;
            this.transientZoomLevel = null;
            this.textView = new TextView(context);
            this.background = new GradientDrawable();
            this.circleSize = circleSize;

            setupTextView();
            this.tabIndex = tabIndex;
        }

        private void setupTextView() {
            String formattedZoom = getFormattedZoom();
            textView.setGravity(Gravity.CENTER);
            textView.setText(formattedZoom);
            textView.setTextSize(12);
            textView.setBackgroundColor(Color.TRANSPARENT);

            int padding = dpToPx(requireContext(), 8);
            textView.setPadding(padding, padding, padding, padding);

            int circlePx = dpToPx(requireContext(), circleSize);
            background.setShape(GradientDrawable.OVAL);
            background.setSize(circlePx, circlePx); // Make it circular

            textView.setBackground(background);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            textView.setLayoutParams(layoutParams);

            setSelected(false);
        }

        @NonNull
        private String getFormattedZoom() {
            String formattedZoom;

            float whichZoom = transientZoomLevel == null ? zoomLevel : transientZoomLevel;

            if (whichZoom < 0) {
                formattedZoom = "0x"; // Handle zoom levels less than 0
            } else if (whichZoom < 10) {
                DecimalFormat formatLessThanTen = new DecimalFormat("#.#x");
                formattedZoom = formatLessThanTen.format(whichZoom); // At most 1 digit after the decimal point if less than 10
            } else {
                DecimalFormat formatTenOrMore = new DecimalFormat("#x");
                formattedZoom = formatTenOrMore.format(whichZoom); // No decimal point if 10 or greater
            }
            return formattedZoom;
        }

        public View getView() {
            return textView;
        }

        public void setSelected(boolean isSelected) {
            textView.setTextColor(isSelected ? Color.BLACK : Color.WHITE);
            background.setColor(isSelected ? ZOOM_BUTTON_COLOR_SELECTED : ZOOM_BUTTON_COLOR_UNSELECTED);
        }

        public float getZoomLevel() {
            return zoomLevel;
        }

        public int getTabIndex() {
            return tabIndex;
        }

        public void setTransientZoomLevel(Float zoomLevel) {
            transientZoomLevel = zoomLevel;
            updateText();
        }

        private void updateText() {
            String formattedZoom = getFormattedZoom();
            textView.setText(formattedZoom);
        }
    }
}

